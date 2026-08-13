package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.deluge.model.request.RemoveTorrent
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoveTorrentTest {

  @Test
  fun `toJson serializes method and params correctly`() {
    val request = RemoveTorrent("torrent_hash_123", removeData = true)
    val json = request.toJson()

    assertEquals("core.remove_torrent", json["method"]?.jsonPrimitive?.content)
    val params = json["params"]?.jsonArray
    assertEquals(2, params?.size)
    assertEquals("torrent_hash_123", params?.get(0)?.jsonPrimitive?.content)
    assertEquals(true, params?.get(1)?.jsonPrimitive?.boolean)
  }

  @Test
  fun `toJson defaults removeData to false`() {
    val request = RemoveTorrent("torrent_hash_456")
    val json = request.toJson()

    val params = json["params"]?.jsonArray
    assertEquals(false, params?.get(1)?.jsonPrimitive?.boolean)
  }
}
