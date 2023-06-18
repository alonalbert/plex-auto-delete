package com.alonalbert.pad.server.config

import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component

@Component
class Config(private val environment: Environment) {
    @Bean
    fun plexConfiguration() = Configuration(
        environment["plex.database.path"] ?: "",
        environment["plex.url"] ?: "",
        environment["plex.section.list"]?.split(",") ?: emptyList(),
    )

    data class Configuration(
        val plexDatabasePath: String,
        val plexUrl: String,
        val plexSections: List<String>,
    )
}
