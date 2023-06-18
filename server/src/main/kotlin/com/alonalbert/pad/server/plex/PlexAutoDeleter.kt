package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.config.Config
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
        val users = userRepository.findAll()

        val plexClient = PlexClient(configuration.plexUrl)
        val sections = plexClient.getSections().filter { it.title in configuration.plexSections && it.type == "show" }

        return users
    }
}
