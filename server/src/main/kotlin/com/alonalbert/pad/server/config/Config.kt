package com.alonalbert.pad.server.config

import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component

private const val PLEX_URL = "plex.url"
private const val PLEX_DATABASE_PATH = "plex.database.path"

@Component
class Config(private val environment: Environment) {
    @Bean
    fun plexConfiguration() = Configuration(
        plexDatabasePath = getPlexDatabasePath(),
        plexUrl = getPlexUrl(),
        plexSections = environment["plex.section.list"]?.split(",")?.toSet() ?: emptySet(),
        autoDeleteDays = environment["plex.auto.delete.days"]?.toInt() ?: 7,
    )

    private fun getPlexDatabasePath(): String {
        return System.getProperty(PLEX_DATABASE_PATH, environment[PLEX_DATABASE_PATH]) ?: throw IllegalStateException("Can't find Plex database")
    }

    private fun getPlexUrl() =
        System.getProperty(PLEX_URL, environment[PLEX_URL]) ?: throw IllegalStateException("Can't find Plex database")

    data class Configuration(
        val plexDatabasePath: String,
        val plexUrl: String,
        val plexSections: Set<String>,
        val autoDeleteDays: Int,
    )
}
