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
      val labelMap = delugeProperties.labels.associate { it.name to it.age.toKotlinDuration() }
      val torrents = client.getTorrents(labelMap.keys)
      val oldTorrents = torrents.filterValues { it.seedingTime > labelMap.getValue(it.label) }
      oldTorrents.forEach { (id, torrent) ->
        logger.info("Removing torrent ${torrent.name}: Seeding for ${torrent.seedingTime}")
      }
    }
  }
}

data class LabelConfig(val name: String, val age: Duration)

@ConfigurationProperties(prefix = "deluge")
data class DelugeProperties(val labels: List<LabelConfig> = emptyList())
