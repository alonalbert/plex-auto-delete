package com.alonalbert.pad.server.importconfig

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.io.File


private val objectMapper = ObjectMapper()

@Suppress("SameParameterValue")
private fun loadFile(configFile: String): ConfigFile = objectMapper.readValue(File(configFile), ConfigFile::class.java)

@Component
class ImportConfig(
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
) {
    fun import() {
        val configFile = loadFile("/home/al/tmp/pad-config.json")
        val users = userRepository.findAll().associateBy { it.name }
        val shows = showRepository.findAll()

        configFile.users.forEach { configUser ->
            val user = users[configUser.name] ?: return@forEach
            val userShows = shows.filter { it.name in configUser.shows.titles }
            val type = when (configUser.shows.type) {
                Shows.Type.INCLUDE -> User.UserType.INCLUDE
                Shows.Type.EXCLUDE -> User.UserType.EXCLUDE
            }
            println("Updating user ${user.name} with ${userShows.size} shows")
            userRepository.save(user.copy(type = type, plexToken = configUser.plexToken, shows = userShows))
        }
    }
}
