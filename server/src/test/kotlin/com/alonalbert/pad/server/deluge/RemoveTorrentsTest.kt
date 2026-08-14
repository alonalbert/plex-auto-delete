package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.deluge.model.request.RemoveTorrents
import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError
import com.alonalbert.pad.server.deluge.model.response.Response
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoveTorrentsTest {

  @Test
  fun `toJson serializes method and params correctly`() {
    val request = RemoveTorrents(listOf("torrent_hash_123"), removeData = true)
    val json = request.toJson()

    assertEquals("core.remove_torrents", json["method"]?.jsonPrimitive?.content)
    val params = json["params"]?.jsonArray
    assertEquals(2, params?.size)
    val ids = params?.get(0)?.jsonArray
    assertEquals(1, ids?.size)
    assertEquals("torrent_hash_123", ids?.get(0)?.jsonPrimitive?.content)
    assertEquals(true, params?.get(1)?.jsonPrimitive?.boolean)
  }

  @Test
  fun `toJson defaults removeData to false`() {
    val request = RemoveTorrents(listOf("torrent_hash_456"))
    val json = request.toJson()

    val params = json["params"]?.jsonArray
    assertEquals(false, params?.get(1)?.jsonPrimitive?.boolean)
  }

  @Test
  fun `deserializes RemoveTorrentError from json array`() {
    val jsonString = """{"result": [["torrent_1", "Failed to remove"], ["torrent_2", "Permission denied"]], "error": null, "id": 1}"""
    val response = Json.decodeFromString<Response<List<RemoveTorrentError>>>(jsonString)

    val errors = response.result
    assertEquals(2, errors?.size)
    assertEquals("torrent_1", errors?.get(0)?.id)
    assertEquals("Failed to remove", errors?.get(0)?.message)
    assertEquals("torrent_2", errors?.get(1)?.id)
    assertEquals("Permission denied", errors?.get(1)?.message)
  }

  @Test
  fun `deserializes empty result on success`() {
    val jsonString = """{"result": [], "error": null, "id": 1}"""
    val response = Json.decodeFromString<Response<List<RemoveTorrentError>>>(jsonString)

    val errors = response.result
    assertEquals(0, errors?.size)
  }
}
