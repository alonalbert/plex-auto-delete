package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.AutoDeleteResult
import com.alonalbert.pad.model.AutoWatchResult
import com.alonalbert.pad.model.User
import com.alonalbert.pad.model.User.UserType.EXCLUDE
import com.alonalbert.pad.model.User.UserType.INCLUDE
import com.alonalbert.pad.server.config.getAutoDeleteDuration
import com.alonalbert.pad.server.config.getPlexSections
import com.alonalbert.pad.server.plex.model.PlexEpisode
import com.alonalbert.pad.server.plex.model.PlexSection
import com.alonalbert.pad.server.plex.model.PlexShow
import com.alonalbert.pad.server.pushover.PushoverClient
import com.alonalbert.pad.server.repository.UserRepository
import com.alonalbert.pad.util.intersect
import com.alonalbert.pad.util.toByteUnitString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.fileSize
import kotlin.time.Duration


@Component
class PlexAutoDeleter(
  environment: Environment,
  private val userRepository: UserRepository,
  private val plexClient: PlexClient,
  private val pushoverClient: PushoverClient,
) {
  private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)
  private val plexSections = environment.getPlexSections()
  private val autoDeleteDuration = environment.getAutoDeleteDuration()

  suspend fun runAutoWatch(): AutoWatchResult {
    logger.info("Running auto watch")

    val sections = plexClient.getMonitoredSections()

    val updatedShows = mutableMapOf<String, MutableList<String>>()
    getActiveUsers()
      .forEach { user ->
        runAutoWatch(user, sections).forEach { show ->
          updatedShows.getOrPut(show) { mutableListOf() }.add(user.name)
        }
      }

    if (updatedShows.isNotEmpty()) {
      val details = updatedShows.entries.joinToString("\n  ") { entry -> "${entry.key}: ${entry.value.joinToString { user -> user }}" }
      pushoverClient.send("Marked watched:\n  $details")
      logger.info("Marked watched:\n  $details")
    }
    return AutoWatchResult(updatedShows)
  }

  suspend fun runAutoDelete(
    watchedDuration: Duration = autoDeleteDuration,
    testOnly: Boolean = false,
  ): AutoDeleteResult {
    logger.info("Running auto delete")

    val shows = plexClient.getMonitoredSections().flatMap { plexClient.getAllShows(it.key) }

    val candidates = withContext(Dispatchers.IO) {
      getActiveUsers().map {
        async { getDeleteCandidates(it, shows, watchedDuration) }
      }.awaitAll()
    }
    val deleteKeys = candidates.map { it.mapTo(hashSetOf()) { candidate -> candidate.key } }.intersect()
    val approvedCandidates = candidates.flatten().filter { it.key in deleteKeys }
    val showsWithDeletions = approvedCandidates.mapTo(hashSetOf()) { it.show }.toList()
    val deleteFiles = approvedCandidates.flatMapTo(hashSetOf()) { it.files }

    var size = 0L
    var count = 0
    deleteFiles.forEach {
      try {
        val path = Path.of(it)
        val fileSize = path.fileSize()
        val updateStats = when (testOnly) {
          true -> testDeleteFile(path)
          false -> deleteFile(path)
        }
        if (updateStats) {
          size += fileSize
          count++
        }
      } catch (e: Throwable) {
        logger.warn("Error deleting $it", e)
      }
    }
    if (count > 0) {
      pushoverClient.send("Deleted ${size.toByteUnitString()} in $count files")
      logger.info("Deleted ${size.toByteUnitString()} in $count files")
    }

    return AutoDeleteResult(count, size, showsWithDeletions)
  }

  suspend fun markUnwatched(user: User, show: String) {
    plexClient.getMonitoredSections().flatMap {
      plexClient.getAllShows(it.key, user.plexToken)
    }.filter {
      it.title == show
    }.forEach {
      logger.info("Marked ${it.title} as unwatched for ${user.name}")
      plexClient.markShowUnwatched(it, user.plexToken)
    }
  }

  suspend fun getUnwatchedForUsers(): Map<String, Map<String, Int>> {
    return getActiveUsers().associateTo(sortedMapOf()) {
      it.name to getUnwatchedForUser(plexClient.getMonitoredSections(), it)
    }
  }

  suspend fun getAllShows(): Map<String, Int> {
    return plexClient.getMonitoredSections()
      .flatMap { plexClient.getAllShows(it.key) }
      .associateTo(sortedMapOf()) { it.title to plexClient.getEpisodes(it.ratingKey).size }
  }

  suspend fun getUnwatchedBy(): Map<String, List<String>> {
    val sections = plexClient.getMonitoredSections()
    val result = sections
      .flatMap { plexClient.getAllShows(it.key)}
      .map { it.title }
      .associateWith { arrayListOf<String>() }
    val unwatchedForUsers = getActiveUsers().associateTo(sortedMapOf<String, List<String>>()) { user ->
      user.name to sections.flatMap { plexClient.getUnwatchedShows(it.key,user.plexToken) }.map { it.title }
    }

    unwatchedForUsers.forEach { (user, shows) ->
      shows.forEach {
        result.getValue(it).add(user)
      }
    }
    return result
  }

  private suspend fun getUnwatchedForUser(sections: List<PlexSection>, user: User): Map<String, Int> {
    return sections
      .flatMap { plexClient.getUnwatchedShows(it.key, user.plexToken) }
      .associateTo(sortedMapOf()) { it.title to plexClient.getUnwatchedEpisodes(it.ratingKey, user.plexToken).size }
  }

  private fun getActiveUsers(): List<User> = userRepository.findAll().filter { it.plexToken.isNotEmpty() }

  private suspend fun PlexClient.getMonitoredSections() =
    getTvSections().filter { it.title in plexSections && it.type == "show" }

  private suspend fun runAutoWatch(
    user: User,
    sections: List<PlexSection>,
  ): List<String> {
    val markedShows = mutableListOf<String>()
    sections.forEach { section ->
      val unwatchedShows = plexClient.getUnwatchedShows(section.key, user.plexToken)
      val userShows = user.shows.mapTo(mutableSetOf()) { it.name }
      val showsToMark = when (user.type) {
        EXCLUDE -> unwatchedShows.filter { it.title in userShows }
        INCLUDE -> unwatchedShows.filter { it.title !in userShows }
      }
      showsToMark.forEach {
        logger.info("Marking watched for user ${user.name}: ${it.title}")
        plexClient.markShowWatched(it, user.plexToken)
      }
      markedShows.addAll(showsToMark.map { it.title })
    }
    return markedShows
  }

  private fun deleteFile(path: Path): Boolean {
    val deleted = path.deleteIfExists()
    when (deleted) {
      true -> logger.info("Deleted: $path")
      false -> logger.info("Does not exist: $path")
    }
    return deleted
  }

  private fun testDeleteFile(path: Path): Boolean {
    logger.info("test deleted: $path")
    return true
  }

  private suspend fun getDeleteCandidates(
    user: User, shows: List<PlexShow>,
    watchedDuration: Duration,
  ): List<DeleteCandidate> {
    val userShows = user.shows.mapTo(hashSetOf()) { it.name }
    return shows.flatMapTo(hashSetOf()) { show ->
      plexClient.getEpisodes(show.ratingKey, user.plexToken)
        .filter { episode -> episode.isDeleteCandidate(user.type, userShows, watchedDuration) }
    }.map { episode ->
      DeleteCandidate(episode.key, episode.showTitle, episode.medias.flatMap { media -> media.parts.map { part -> part.file } })
    }
  }

  private fun PlexEpisode.isDeleteCandidate(
    type: User.UserType,
    showNames: Set<String>,
    watchedDuration: Duration,
  ): Boolean {
    val isIgnored = when (type) {
      EXCLUDE -> showTitle in showNames
      INCLUDE -> showTitle !in showNames
    }
    val cutoff = Clock.System.now().minus(watchedDuration)
    return isIgnored || (lastViewedAt < cutoff && viewCount > 0)
  }

  private data class DeleteCandidate(val key: String, val show: String, val files: List<String>)
}

fun main() {
  val users = mapOf(
    "User1" to listOf("Show1", "Show2"),
    "User2" to listOf("Show1", "Show3"),
    "User3" to listOf("Show3"),
  )

  val results = getResults(users)

  println(results)

}


private fun getResults(users: Map<String, List<String>>): Map<String, List<String>> {
  val map: MutableMap<String, MutableList<String>> = mutableMapOf()
  users.forEach { (user, shows) ->
    shows.forEach { show ->
      map.getOrPut(show) { mutableListOf() }.add(user)
    }
  }
  return map
}