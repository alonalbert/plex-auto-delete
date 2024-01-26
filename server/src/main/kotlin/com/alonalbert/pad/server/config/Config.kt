package com.alonalbert.pad.server.config

import org.springframework.core.env.Environment
import kotlin.time.Duration.Companion.days

private const val PLEX_URL = "plex.url"
private const val PLEX_DATABASE_PATH = "plex.database.path"
private const val PLEX_SECTION_LIST = "plex.section.list"
private const val PLEX_AUTO_DELETE_DAYS = "plex.auto.delete.days"
private const val PUSHOVER_TOKEN = "pushover.token"
private const val PUSHOVER_USER_TOKEN = "pushover.user.token"
private const val SONARR_URL = "sonarr.url"
private const val SONARR_USERNAME = "sonarr.username"
private const val SONARR_PASSWORD = "sonarr.password"
private const val SONARR_API_KEY = "sonarr.api.key"
private const val TEST_MODE = "test.mode"

fun Environment.getPlexDatabasePath() = getConfigProperty(PLEX_DATABASE_PATH)
fun Environment.getPlexUrl() = getConfigProperty(PLEX_URL)
fun Environment.getPlexSections() = getConfigProperty(PLEX_SECTION_LIST, "").split(",").toList().distinct()
fun Environment.getAutoDeleteDuration() = getConfigProperty(PLEX_AUTO_DELETE_DAYS, "7").toInt().days

fun Environment.getPushoverToken() = getConfigProperty(PUSHOVER_TOKEN)
fun Environment.getPushoverUserToken() = getConfigProperty(PUSHOVER_USER_TOKEN)

fun Environment.getSonarrUrl() = getConfigProperty(SONARR_URL)
fun Environment.getSonarrUsername() = getConfigProperty(SONARR_USERNAME)
fun Environment.getSonarrPassword() = getConfigProperty(SONARR_PASSWORD)
fun Environment.getSonarrApiKey() = getConfigProperty(SONARR_API_KEY)

fun Environment.isTestMode() = getConfigProperty(TEST_MODE, "false").toBoolean()

private fun Environment.getConfigProperty(name: String, defaultValue: String? = null) =
  System.getProperty(name, getProperty(name)) ?: defaultValue ?: throw IllegalStateException("Can't find property $name")

