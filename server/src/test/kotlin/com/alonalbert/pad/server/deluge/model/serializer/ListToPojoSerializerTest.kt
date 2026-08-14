package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.Host
import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

object TestPojoWithNullableSerializer : ListToPojoSerializer<TestPojoWithNullable>(TestPojoWithNullable::class)

@Serializable(with = TestPojoWithNullableSerializer::class)
data class TestPojoWithNullable(
  val id: String,
  val count: Int,
  val extra: String?,
)

object TestPojoInvalidOrderSerializer : ListToPojoSerializer<TestPojoInvalidOrder>(TestPojoInvalidOrder::class)

@Serializable(with = TestPojoInvalidOrderSerializer::class)
data class TestPojoInvalidOrder(
  val optional: String?,
  val required: String,
)

class ListToPojoSerializerTest {

  @Test
  fun `deserializes RemoveTorrentError with ListToPojoSerializer`() {
    val jsonString = """["torrent_1", "Failed to remove"]"""
    val (id, message) = Json.decodeFromString(ListToPojoSerializer(RemoveTorrentError::class), jsonString)

    assertEquals("torrent_1", id)
    assertEquals("Failed to remove", message)
  }

  @Test
  fun `serializes RemoveTorrentError with ListToPojoSerializer`() {
    val error = RemoveTorrentError("torrent_1", "Failed to remove")
    val jsonString = Json.encodeToString(ListToPojoSerializer(RemoveTorrentError::class), error)

    assertEquals("""["torrent_1","Failed to remove"]""", jsonString)
  }

  @Test
  fun `deserializes Host with status present`() {
    val jsonString = """["host_id", "localhost", 58846, "admin", "Online"]"""
    val host = Json.decodeFromString(ListToPojoSerializer(Host::class), jsonString)

    assertEquals("host_id", host.id)
    assertEquals("localhost", host.hostname)
    assertEquals(58846, host.port)
    assertEquals("admin", host.username)
    assertEquals("Online", host.status)
  }

  @Test
  fun `deserializes Host with status omitted`() {
    val jsonString = """["host_id", "localhost", 58846, "admin"]"""
    val host = Json.decodeFromString(ListToPojoSerializer(Host::class), jsonString)

    assertEquals("host_id", host.id)
    assertEquals("localhost", host.hostname)
    assertEquals(58846, host.port)
    assertEquals("admin", host.username)
    assertNull(host.status)
  }

  @Test
  fun `deserializes Host with status explicit null`() {
    val jsonString = """["host_id", "localhost", 58846, "admin", null]"""
    val host = Json.decodeFromString(ListToPojoSerializer(Host::class), jsonString)

    assertEquals("host_id", host.id)
    assertEquals("localhost", host.hostname)
    assertEquals(58846, host.port)
    assertEquals("admin", host.username)
    assertNull(host.status)
  }

  @Test
  fun `serializes Host with status present`() {
    val host = Host("host_id", "localhost", 58846, "admin", "Online")
    val jsonString = Json.encodeToString(ListToPojoSerializer(Host::class), host)

    assertEquals("""["host_id","localhost",58846,"admin","Online"]""", jsonString)
  }

  @Test
  fun `serializes Host with status null`() {
    val host = Host("host_id", "localhost", 58846, "admin", null)
    val jsonString = Json.encodeToString(ListToPojoSerializer(Host::class), host)

    assertEquals("""["host_id","localhost",58846,"admin",null]""", jsonString)
  }

  @Test
  fun `throws exception when json array has fewer elements than required fields`() {
    val jsonString = """["host_id", "localhost", 58846]"""
    val exception = assertThrows(IllegalArgumentException::class.java) {
      Json.decodeFromString(ListToPojoSerializer(Host::class), jsonString)
    }
    assertEquals("Host JSON array must contain at least 4 elements, got 3", exception.message)
  }

  @Test
  fun `throws exception when non-nullable field follows nullable field`() {
    val exception = assertThrows(IllegalArgumentException::class.java) {
      ListToPojoSerializer(TestPojoInvalidOrder::class)
    }
    assertEquals(
      "Nullable fields must be the last fields by order in com.alonalbert.pad.server.deluge.model.serializer.TestPojoInvalidOrder: field 'required' is non-nullable but follows nullable field(s)",
      exception.message
    )
  }

  @Test
  fun `works when used in class annotation via object serializer subclass`() {
    val jsonString = """["abc", 123, "optional_val"]"""
    val pojo = Json.decodeFromString<TestPojoWithNullable>(jsonString)

    assertEquals("abc", pojo.id)
    assertEquals(123, pojo.count)
    assertEquals("optional_val", pojo.extra)
  }
}
