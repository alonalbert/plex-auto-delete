package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.Show
import com.alonalbert.pad.model.User
import com.alonalbert.pad.model.User.UserType.EXCLUDE
import com.alonalbert.pad.model.User.UserType.INCLUDE
import com.alonalbert.pad.server.config.Config
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
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.days


@Component
class PlexAutoDeleter(
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
    private val configuration: Config.Configuration,
) {
    private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)

    suspend fun runAutoWatcher(): List<User> {
        logger.info("Running auto watcher")
        val plexClient = PlexClient(configuration.plexUrl)

        val sections = plexClient.getMonitoredSections()
        val allShows = showRepository.findAll().associateBy { it.name }
        return userRepository.findAll().map {
            runAutoWatcher(it, sections, allShows)
        }.filter { it.shows.isNotEmpty() }
    }

    private suspend fun PlexClient.getMonitoredSections() =
        getTvSections().filter { it.title in configuration.plexSections && it.type == "show" }

    private suspend fun runAutoWatcher(
        user: User,
        sections: List<PlexSection>,
        allShows: Map<String, Show>
    ): User {
        val plexClient = PlexClient(configuration.plexUrl, user.plexToken)
        val markedShows = mutableListOf<Show>()
        sections.forEach { section ->
            val unwatchedShows = plexClient.getUnwatchedShows(section.key)
            val userShows = user.shows.mapTo(mutableSetOf()) { it.name }
            val showsToMark = when (user.type) {
                EXCLUDE -> unwatchedShows.filter { it.title in userShows }
                INCLUDE -> unwatchedShows.filter { it.title !in userShows }
            }
            showsToMark.forEach {
                logger.debug("Marking watched for user {}: {}", user.name, { it.title })
                plexClient.markShowWatched(it)
            }
            markedShows.addAll(showsToMark.mapNotNull { allShows[it.title] })
        }
        return user.copy(shows = markedShows)
    }

    suspend fun runAutoDeleter(): String {
        val plexClient = PlexClient(configuration.plexUrl)
        val shows = plexClient.getMonitoredSections().flatMap { plexClient.getAllShows(it.key) }

        withContext(Dispatchers.IO) {
            val candidates = userRepository.findAll().map {
                async { getDeleteCandidates(it, shows) }
            }.awaitAll()
            val deleteKeys = candidates.map { it.mapTo(hashSetOf()) { candidate -> candidate.key } }.intersect()
            val deleteFiles = candidates.flatten().filter { it.key in deleteKeys }.flatMap { it.files }
            deleteFiles.forEach {
                logger.info("Deleting file $it")
            }
        }
        return ""
    }

    private suspend fun getDeleteCandidates(user: User, shows: List<PlexShow>): List<DeleteCandidate> {
        val plexClient = PlexClient(configuration.plexUrl, user.plexToken)
        val userShows = user.shows.mapTo(hashSetOf()) { it.name }
        return shows.flatMapTo(hashSetOf()) { show ->
            plexClient.getEpisodes(show.ratingKey).filter { episode -> episode.isDeleteCandidate(user.type, userShows) }
        }.map { episode ->
            DeleteCandidate(episode.key, episode.medias.flatMap { media -> media.parts.map { part -> part.file } })
        }
    }

    private fun PlexEpisode.isDeleteCandidate(
        type: User.UserType,
        showNames: Set<String>
    ): Boolean {
        val isIgnored = when (type) {
            EXCLUDE -> showTitle in showNames
            INCLUDE -> showTitle !in showNames
        }
        val cutoff = Clock.System.now().minus(configuration.autoDeleteDays.days)
        return isIgnored || (lastViewedAt < cutoff && viewCount > 0)
    }

    private data class DeleteCandidate(val key: String, val files: List<String>)
}
