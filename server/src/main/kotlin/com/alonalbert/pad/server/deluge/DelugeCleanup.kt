package com.alonalbert.pad.server.deluge

import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.time.toKotlinDuration

@Component
@EnableConfigurationProperties(DelugeProperties::class)
class DelugeCleanup(
  private val environment: Environment,
  private val delugeProperties: DelugeProperties,
) {
  private val logger = LoggerFactory.getLogger(DelugeCleanup::class.java)

  suspend fun cleanup() = coroutineScope {
    logger.info("Removing old torrents")
    DelugeClient(environment).use { client ->
      val labelMap = delugeProperties.labels.associateBy { it.name }
      val torrents = client.getTorrents(labelMap.keys)
      val oldTorrents = torrents.filterValues { it.seedingTime > labelMap.getValue(it.label).age }
      oldTorrents.values.forEach {
        logger.info("Removing torrent ${it.name}: Seeding for ${it.seedingTime}")
      }
      val (withData, withoutData) = oldTorrents.entries
        .partition { (_, torrent) -> labelMap.getValue(torrent.label).removeData }

      client.removeTorrents(withData.map { it.key }, removeData = true)
      client.removeTorrents(withoutData.map { it.key }, removeData = false)
    }
  }
}

data class LabelConfig(val name: String, val javaAge: Duration, val removeData: Boolean) {
  val age = javaAge.toKotlinDuration()
}

@ConfigurationProperties(prefix = "deluge")
data class DelugeProperties(val labels: List<LabelConfig> = emptyList())
