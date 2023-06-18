package com.alonalbert.pad.server.plex

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class PlexAutoDeleter(
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
) {
    private val logger = LoggerFactory.getLogger(PlexAutoDeleter::class.java)

    fun markWatched(): List<User> {
        TODO()
    }
}