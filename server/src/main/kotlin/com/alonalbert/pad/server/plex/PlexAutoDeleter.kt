package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.Show
import com.alonalbert.pad.model.User
import com.alonalbert.pad.model.User.UserType.EXCLUDE
import com.alonalbert.pad.model.User.UserType.INCLUDE
import com.alonalbert.pad.server.config.Config
import com.alonalbert.pad.server.plex.model.Section
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class PlexAutoDeleter(
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
    private val configuration: Config.Configuration,
) {
    private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)

    fun runAutoWatcher(): List<User> {
        logger.info("Running auto watcher")
        val plexClient = PlexClient(configuration.plexUrl)

        val sections = plexClient.getTvSections().filter { it.title in configuration.plexSections && it.type == "show" }
        val allShows = showRepository.findAll().associateBy { it.name }
        return userRepository.findAll().map {
            runAutoWatcher(it, sections, allShows)
        }.filter { it.shows.isNotEmpty() }
    }

    private fun runAutoWatcher(user: User, sections: List<Section>, allShows: Map<String, Show>): User {
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
}
