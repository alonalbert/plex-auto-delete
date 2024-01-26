package com.alonalbert.pad.server.controller

import com.alonalbert.pad.model.AutoDeleteResult
import com.alonalbert.pad.model.AutoWatchResult
import com.alonalbert.pad.server.plex.PlexAutoDeleter
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.days

@RestController
@RequestMapping("/api/action")
class ActionController(
  @Autowired private val plexAutoDeleter: PlexAutoDeleter,
) {
  @GetMapping("/auto-watch")
  fun autoWatch(): AutoWatchResult = runBlocking { plexAutoDeleter.runAutoWatch() }

  @GetMapping("/auto-delete")
  fun autoDelete(
    @RequestParam(name = "days") days: Int,
    @RequestParam(name = "isTestMode") isTestMode: Boolean,
  ): AutoDeleteResult = runBlocking { plexAutoDeleter.runAutoDelete(days.days, isTestMode) }
}
