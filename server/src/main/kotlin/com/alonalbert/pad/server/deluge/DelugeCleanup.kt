package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.deluge.model.config.DelugeProperties
import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError
import com.alonalbert.pad.server.deluge.model.response.Torrent
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(DelugeCleanup::class.java)

@Component
@EnableConfigurationProperties(DelugeProperties::class)
class DelugeCleanup(
  private val environment: Environment,
  delugeProperties: DelugeProperties,
) {
  private val labelConfig = delugeProperties.labels.associateBy { it.name }

  suspend fun cleanup() = coroutineScope {
    logger.info("Removing old torrents")
    DelugeClient(environment).use { client ->
      val torrents = client.getTorrents(labelConfig.keys)
      val remove = torrents.filter { it.seedingTime > labelConfig.getValue(it.label).age }
      client.removeTorrents(remove)
      val removed = remove.mapTo(mutableSetOf()) { it.id }
      client.removeExcessiveTorrents(torrents.filter { it.id !in removed })
    }
  }

  private suspend fun DelugeClient.removeExcessiveTorrents(torrents: List<Torrent>) {
    val labels = labelConfig.filterValues { it.maxTorrents != null }.mapTo(mutableSetOf()) { it.value.name }
    val remove = torrents.filter { it.label in labels && it.seedingTime.isPositive() }.groupBy { it.label }
      .flatMap { (label, torrents) ->
        val maxTorrents = labelConfig[label]?.maxTorrents ?: return@flatMap emptyList()
        if (torrents.size <= maxTorrents) {
          return@flatMap emptyList()
        }
        torrents.sortedBy { it.seedingTime }.drop(maxTorrents)
      }
    removeTorrents(remove)
  }

  private suspend fun DelugeClient.removeTorrents(torrents: List<Torrent>) {
    torrents.forEach { logger.info("Removing torrent ${it.name}: Seeding for ${it.seedingTime}") }
    val (withData, withoutData) = torrents.partition { labelConfig.getValue(it.label).removeData }
    removeTorrents(withData.map { it.id }, removeData = true).warn(torrents)
    removeTorrents(withoutData.map { it.id }, removeData = false).warn(torrents)
  }
}

private fun List<RemoveTorrentError>.warn(torrents: List<Torrent>) = forEach { error ->
  logger.warn("Error removing ${torrents.find { error.id == it.id }}: ${error.message}")
}
