package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.Host
import com.alonalbert.pad.server.utils.toJsonElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object HostSerializer : KSerializer<Host> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Host")

  override fun deserialize(decoder: Decoder): Host {
    val jsonDecoder = decoder as? JsonDecoder ?: error("HostSerializer can only be used with JSON decoding")
    val array = jsonDecoder.decodeJsonElement().jsonArray
    require(array.size >= 4) {
      "Host JSON array must contain at least 4 elements, got ${array.size}"
    }

    return Host(
      id = array[0].jsonPrimitive.content,
      hostname = array[1].jsonPrimitive.content,
      port = array[2].jsonPrimitive.int,
      username = array[3].jsonPrimitive.content,
      status = array.getOrNull(4)?.jsonPrimitive?.content
    )
  }

  override fun serialize(encoder: Encoder, value: Host) {
    val jsonEncoder = encoder as? JsonEncoder ?: error("HostSerializer can only be used with JSON encoding")
    val array = buildJsonArray {
      add(value.id.toJsonElement())
      add(value.hostname.toJsonElement())
      add(value.port.toJsonElement())
      add(value.username.toJsonElement())
      add(value.status.toJsonElement())
    }

    jsonEncoder.encodeJsonElement(array)
  }
}