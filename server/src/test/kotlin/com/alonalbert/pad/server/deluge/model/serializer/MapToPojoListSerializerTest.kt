package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.Response
import com.alonalbert.pad.server.deluge.model.response.Torrent
import com.alonalbert.pad.server.deluge.model.response.Torrents
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class MapToPojoListSerializerTest {

  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun `deserializes map of torrents to list of Torrent POJOs with id`() {
    val jsonString = """
      {
        "result": {
          "hash_123": {
            "name": "Ubuntu 22.04",
            "label": "linux",
            "seeding_time": 3600
          },
          "hash_456": {
            "name": "Debian 12",
            "label": "linux",
            "seeding_time": 7200
          }
        },
        "error": null,
        "id": 1
      }
    """.trimIndent()

    val response = json.decodeFromString<Response<Torrents>>(jsonString)
    val torrents = response.result

    assertEquals(2, torrents?.size)

    val torrent1 = torrents!![0]
    assertEquals("hash_123", torrent1.id)
    assertEquals("Ubuntu 22.04", torrent1.name)
    assertEquals("linux", torrent1.label)
    assertEquals(3600.seconds, torrent1.seedingTime)

    val torrent2 = torrents[1]
    assertEquals("hash_456", torrent2.id)
    assertEquals("Debian 12", torrent2.name)
    assertEquals("linux", torrent2.label)
    assertEquals(7200.seconds, torrent2.seedingTime)
  }

  @Test
  fun `serializes list of Torrent POJOs back to map structure`() {
    val torrents = Torrents(
      listOf(
        Torrent("hash_123", "Ubuntu 22.04", "linux", 3600.seconds),
        Torrent("hash_456", "Debian 12", "linux", 7200.seconds)
      )
    )

    val serializedJson = json.encodeToString(TorrentListSerializer, torrents)
    val expectedJson = """{"hash_123":{"name":"Ubuntu 22.04","label":"linux","seeding_time":3600},"hash_456":{"name":"Debian 12","label":"linux","seeding_time":7200}}"""

    assertEquals(expectedJson, serializedJson)
  }
}
