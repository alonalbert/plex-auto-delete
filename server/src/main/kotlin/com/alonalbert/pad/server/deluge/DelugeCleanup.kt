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
  private val delugeProperties: DelugeProperties,
) {

  suspend fun cleanup() = coroutineScope {
    logger.info("Removing old torrents")
    DelugeClient(environment).use { client ->
      val labelMap = delugeProperties.labels.associateBy { it.name }
      val torrents = client.getTorrents(labelMap.keys)
      val oldTorrents = torrents.filter { it.seedingTime > labelMap.getValue(it.label).age }
      oldTorrents.forEach {
        logger.info("Removing torrent ${it.name}: Seeding for ${it.seedingTime}")
      }
      val (withData, withoutData) = oldTorrents
        .partition { labelMap.getValue(it.label).removeData }

      client.removeTorrents(withData.map { it.id }, removeData = true).warn(oldTorrents)
      client.removeTorrents(withoutData.map { it.id }, removeData = false).warn(oldTorrents)
    }
  }

}

private fun List<RemoveTorrentError>.warn(torrents: List<Torrent>) {
  val torrentMap = torrents.associateBy { it.id }
  forEach {
    logger.warn("Error removing ${torrentMap[it.id]}: ${it.message}")
  }
}
