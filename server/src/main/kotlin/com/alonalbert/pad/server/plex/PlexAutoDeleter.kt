package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.AutoDeleteResult
import com.alonalbert.pad.model.AutoWatchResult
import com.alonalbert.pad.model.Show
import com.alonalbert.pad.model.User
import com.alonalbert.pad.model.User.UserType.EXCLUDE
import com.alonalbert.pad.model.User.UserType.INCLUDE
import com.alonalbert.pad.server.config.getAutoDeleteDuration
import com.alonalbert.pad.server.config.getPlexSections
import com.alonalbert.pad.server.plex.model.PlexEpisode
import com.alonalbert.pad.server.plex.model.PlexSection
import com.alonalbert.pad.server.plex.model.PlexShow
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.repository.UserRepository
import com.alonalbert.pad.util.intersect
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
    private val showRepository: ShowRepository,
    private val plexClient: PlexClient,
) {
    private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)
    private val plexSections = environment.getPlexSections()
    private val autoDeleteDuration = environment.getAutoDeleteDuration()

    suspend fun runAutoWatch(): AutoWatchResult {
        logger.info("Running auto watcher")

        val sections = plexClient.getMonitoredSections()
        val allShows = showRepository.findAll().associateBy { it.name }
        val result = userRepository.findAll()
            .map {
                runAutoWatch(it, sections, allShows)
            }
            .filter { it.shows.isNotEmpty() }
            .associate { user -> user.name to user.shows.map { show -> show.name } }
        return AutoWatchResult(result)
    }

    private suspend fun PlexClient.getMonitoredSections() =
        getTvSections().filter { it.title in plexSections && it.type == "show" }

    private suspend fun runAutoWatch(
        user: User,
        sections: List<PlexSection>,
        allShows: Map<String, Show>
    ): User {
        val markedShows = mutableListOf<Show>()
        sections.forEach { section ->
            val unwatchedShows = plexClient.getUnwatchedShows(section.key, user.plexToken)
            val userShows = user.shows.mapTo(mutableSetOf()) { it.name }
            val showsToMark = when (user.type) {
                EXCLUDE -> unwatchedShows.filter { it.title in userShows }
                INCLUDE -> unwatchedShows.filter { it.title !in userShows }
            }
            showsToMark.forEach {
                logger.debug("Marking watched for user {}: {}", user.name, { it.title })
                plexClient.markShowWatched(it, user.plexToken)
            }
            markedShows.addAll(showsToMark.mapNotNull { allShows[it.title] })
        }
        return user.copy(shows = markedShows)
    }

    suspend fun runAutoDelete(
        watchedDuration: Duration = autoDeleteDuration,
        testOnly: Boolean = false,
    ): AutoDeleteResult {
        val shows = plexClient.getMonitoredSections().flatMap { plexClient.getAllShows(it.key) }

        val candidates = withContext(Dispatchers.IO) {
            userRepository.findAll().map {
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
        return AutoDeleteResult(count, size, showsWithDeletions)
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
