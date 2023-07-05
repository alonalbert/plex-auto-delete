package com.alonalbert.pad.server.controller

import com.alonalbert.pad.model.Show
import com.alonalbert.pad.server.config.getPlexSections
import com.alonalbert.pad.server.plex.PlexClient
import com.alonalbert.pad.server.repository.ShowRepository
import com.alonalbert.pad.server.sonarr.SonarrClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ShowController(
    private val environment: Environment,
    private val showRepository: ShowRepository,
    private val plexClient: PlexClient,
    private val sonarrClient: SonarrClient,
) {
    private val logger = LoggerFactory.getLogger(ShowController::class.java)

    @GetMapping("/shows")
    fun getShows(): List<Show> {
        updateShows()
        return showRepository.findAll()
    }

    private suspend fun getPlexShows(): List<String> = withContext(Dispatchers.IO) {
        val plexSections = environment.getPlexSections().toHashSet()
        plexClient.getTvSections().filter { it.title in plexSections }.flatMap { plexClient.getAllShows(it.key) }.map { it.title }
    }

    private suspend fun getSonarrShows(): List<String> = withContext(Dispatchers.IO) {
        sonarrClient.getShows()
    }

    private fun updateShows() {
        val currentShows = runBlocking {
            getPlexShows() + getSonarrShows()
        }.toHashSet()

        val shows = showRepository.findAll().associateBy { it.name }
        val newShows = currentShows - shows.keys
        val deletedShows = (shows.keys - currentShows).mapNotNull { shows[it] }

        if (newShows.isNotEmpty()) {
            logger.info("Adding new shows:\n  ${newShows.joinToString("\n  ") { it }}")
            showRepository.saveAll(newShows.map { Show(name = it) })
        }
        if (deletedShows.isNotEmpty()) {
            showRepository.deleteAllById(deletedShows.map { it.id })
        }
        val dups = showRepository.findAll().groupBy { it.name }.filter { it.value.size > 1 }
        dups.values.map { it.drop(1) }.flatten().forEach { showRepository.deleteById(it.id) }
    }
}
