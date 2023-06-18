package com.alonalbert.pad.server.controller

import com.alonalbert.pad.model.User
import com.alonalbert.pad.server.plex.PlexAutoDeleter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/action")
class ActionController(
    @Autowired private val plexAutoDeleter: PlexAutoDeleter,
) {
    @GetMapping("/mark-watched")
    fun markWatched(): List<User> = plexAutoDeleter.markWatched()
}