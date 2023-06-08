package com.alonalbert.pad.server.config

import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Component

@Component
class Config(private val environment: Environment) {
    @Bean
    fun plexDatabasePath() = environment["plex.database.path"]
}
