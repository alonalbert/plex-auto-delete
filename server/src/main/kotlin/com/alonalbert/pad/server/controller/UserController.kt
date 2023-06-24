package com.alonalbert.pad.server.controller

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.config.getPlexDatabasePath
import com.alonalbert.pad.server.repository.UserRepository
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.DriverManager

@RestController
@RequestMapping("/api")
class UserController(
    environment: Environment,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)
    private val plexDatabasePath = environment.getPlexDatabasePath()

    @GetMapping("/users")
    fun getUsers(): List<User> {
        updateUsersFromPlex()
        return userRepository.findAll()
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable(value = "id") id: Long,
        @Valid @RequestBody value: User
    ): ResponseEntity<User> {
        updateUsersFromPlex()
        return userRepository.findById(id).map { existing ->
            val updated = existing
                .copy(name = value.name, type = value.type, plexToken = value.plexToken, shows = value.shows)
            ResponseEntity.ok().body(userRepository.save(updated))
        }.orElse(ResponseEntity.notFound().build())

    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable(value = "id") id: Long): ResponseEntity<Void> {
        updateUsersFromPlex()
        return userRepository.findById(id).map { existing ->
            userRepository.delete(existing)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }

    private fun updateUsersFromPlex() {
        val plexUsers = DriverManager.getConnection("jdbc:sqlite:${plexDatabasePath}").use { connection ->
            buildSet {
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM accounts WHERE name != '' ORDER BY created_at").use {
                        while (it.next()) {
                            val name = it.getString("name")
                            add(name)
                        }
                    }
                }
            }
        }
        val users = userRepository.findAll().associateBy { it.name }
        val newUsers = plexUsers - users.keys
        val deletedUsers = users.keys - plexUsers

        if (newUsers.isNotEmpty()) {
            logger.info("Adding new Plex users: [${newUsers.joinToString { it }}]")
            userRepository.saveAll(newUsers.map { User(name = it) })
        }
        if (deletedUsers.isNotEmpty()) {
            logger.info("Users deleted from Plex: [${deletedUsers.joinToString { it }}]")
            userRepository.deleteAllById(deletedUsers.mapNotNull { users[it]?.id })
        }
    }
}
