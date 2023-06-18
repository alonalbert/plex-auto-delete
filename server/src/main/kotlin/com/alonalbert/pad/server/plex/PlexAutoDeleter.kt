package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.config.Config
import com.alonalbert.pad.server.plex.model.Section
import com.alonalbert.pad.server.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class PlexAutoDeleter(
    private val userRepository: UserRepository,
    private val configuration: Config.Configuration,
) {
    private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)

    fun runAutoWatcher(): List<User> {
        logger.info("Running auto watcher")
        val plexClient = PlexClient(configuration.plexUrl)

        val sections = plexClient.getTvSections().filter { it.title in configuration.plexSections && it.type == "show" }

        return userRepository.findAll().map {
            runAutoWatcher(it, sections)
        }
    }

    private fun runAutoWatcher(user: User, sections: List<Section>): User {
        val plexClient = PlexClient(configuration.plexUrl, user.plexToken)

        return user.copy(shows = emptyList())
    }
}
