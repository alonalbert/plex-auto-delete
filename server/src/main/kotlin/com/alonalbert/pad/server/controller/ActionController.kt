package com.alonalbert.pad.server.controller

import com.alonalbert.pad.model.AutoDeleteResult
import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.plex.PlexAutoDeleter
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/action")
class ActionController(
    @Autowired private val plexAutoDeleter: PlexAutoDeleter,
) {
    @GetMapping("/auto-watch")
    fun autoWatch(): List<User> = runBlocking { plexAutoDeleter.runAutoWatch() }

    @GetMapping("/auto-delete")
    fun autoDelete(): AutoDeleteResult = runBlocking { plexAutoDeleter.runAutoDelete() }
}