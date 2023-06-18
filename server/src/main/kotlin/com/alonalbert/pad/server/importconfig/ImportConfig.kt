package com.alonalbert.pad.server.importconfig

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import java.io.File


private val objectMapper = ObjectMapper()

@Suppress("SameParameterValue")
private fun loadFile(configFile: String): ConfigFile = objectMapper.readValue(File(configFile), ConfigFile::class.java)

@SpringBootApplication
@EntityScan("com.alonalbert.pad.*")
@ComponentScan("com.alonalbert.pad.*")
class ImportConfig(
    private val userRepository: UserRepository,
    private val showRepository: ShowRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val configFile = loadFile("/home/aalbert/tmp/pad-config.json")
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
            userRepository.save(user.copy(type = type, shows = userShows))
        }
    }
}

fun main() {
    SpringApplication.run(ImportConfig::class.java).close()
}
