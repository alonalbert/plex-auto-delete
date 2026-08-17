package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.deluge.model.config.DelugeProperties
import com.alonalbert.pad.server.deluge.model.response.Torrent
import com.alonalbert.pad.server.pushover.PushoverClient
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(DelugeCleanup::class.java)

private val SEASON_EPISODE_REGEX = Regex("(?i)s\\d+e\\d+")

@Component
@EnableConfigurationProperties(DelugeProperties::class)
class DelugeCleanup(
  private val environment: Environment,
  delugeProperties: DelugeProperties,
  private val pushoverClient: PushoverClient,
) {
  private val labelConfig = delugeProperties.labels.associateBy { it.name }

  suspend fun cleanup() = coroutineScope {
    logger.info("Removing old torrents")
    DelugeClient(environment).use { client ->
      val torrents = client.getTorrents(labelConfig.keys)
      val oldTorrents = torrents.filter { it.seedingTime > labelConfig.getValue(it.label).age }
      val oldIds = oldTorrents.mapTo(mutableSetOf()) { it.id }
      val excessiveTorrents = findExcessiveTorrents(torrents.filter { it.id !in oldIds })

      client.removeTorrents(oldTorrents + excessiveTorrents)
    }
  }

  private suspend fun findExcessiveTorrents(torrents: List<Torrent>): List<Torrent> {
    val labels = labelConfig.filterValues { it.maxTorrents != null }.mapTo(mutableSetOf()) { it.value.name }
    return torrents.filter { it.label in labels && it.seedingTime.isPositive() }.groupBy { it.label }
      .flatMap { (label, torrents) ->
        val maxTorrents = labelConfig[label]?.maxTorrents ?: return@flatMap emptyList()
        if (torrents.size <= maxTorrents) {
          return@flatMap emptyList()
        }
        torrents.sortedBy { it.seedingTime }.drop(maxTorrents)
      }
  }

  private suspend fun DelugeClient.removeTorrents(torrents: List<Torrent>) {
    val map = torrents.associate { it.id to it.name }
    torrents.forEach { logger.info("Removing torrent ${it.name}: Seeding for ${it.seedingTime}") }
    val (withData, withoutData) = torrents.partition { labelConfig.getValue(it.label).removeData }
    val errors = removeTorrents(withData.map { it.id }, removeData = true) + removeTorrents(withoutData.map { it.id }, removeData = false)
    errors.forEach {
      logger.warn("Error removing ${map[it.id]}: ${it.message}")
    }
    if (errors.isNotEmpty()) {
      pushoverClient.send("Error removing torrents\n  ${errors.joinToString("\n  ") { "${map[it.id]}: ${it.message}" }}")
    }
    val errorIds = errors.mapTo(mutableSetOf()) { it.id }
    val removed = torrents.filter { it.id !in errorIds }
    if (removed.isNotEmpty()) {
      pushoverClient.send("Removed torrents\n  ${removed.joinToString("\n  ") { it.sanitizedName }}")
    }
  }
}

private val Torrent.sanitizedName: String
  get() {
    val match = SEASON_EPISODE_REGEX.find(name)
    val truncated = when (match != null) {
      true -> name.substring(0, match.range.last + 1)
      false -> name
    }
    return truncated.replace('.', ' ').replace('-', ' ')
}
