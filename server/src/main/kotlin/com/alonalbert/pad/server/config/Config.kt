package com.alonalbert.pad.server.config

import org.springframework.core.env.Environment
import kotlin.time.Duration.Companion.days

private const val PLEX_URL = "plex.url"
private const val PLEX_DATABASE_PATH = "plex.database.path"
private const val PLEX_SECTION_LIST = "plex.section.list"
private const val PLEX_AUTO_DELETE_DAYS = "plex.auto.delete.days"
private const val PUSHOVER_TOKEN = "pushover.token"
private const val PUSHOVER_USER_TOKEN = "pushover.user.token"

fun Environment.getPlexDatabasePath() = getConfigProperty(PLEX_DATABASE_PATH)
fun Environment.getPlexUrl() = getConfigProperty(PLEX_URL)
fun Environment.getPlexSections() = getConfigProperty(PLEX_SECTION_LIST, "").split(",").toList().distinct()
fun Environment.getAutoDeleteDuration() = getConfigProperty(PLEX_AUTO_DELETE_DAYS, "7").toInt().days
fun Environment.getPushoverToken() = getConfigProperty(PUSHOVER_TOKEN)
fun Environment.getPushoverUserToken() = getConfigProperty(PUSHOVER_USER_TOKEN)

private fun Environment.getConfigProperty(name: String, defaultValue: String? = null) =
    System.getProperty(name, getProperty(name)) ?: defaultValue ?: throw IllegalStateException("Can't find property $name")

