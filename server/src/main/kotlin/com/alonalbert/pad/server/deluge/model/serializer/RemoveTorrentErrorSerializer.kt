package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError
import com.alonalbert.pad.server.utils.toJsonElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object RemoveTorrentErrorSerializer : KSerializer<RemoveTorrentError> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RemoveTorrentError")

  override fun deserialize(decoder: Decoder): RemoveTorrentError {
    val jsonDecoder = decoder as? JsonDecoder ?: error("RemoveTorrentErrorSerializer can only be used with JSON decoding")
    val array = jsonDecoder.decodeJsonElement().jsonArray
    require(array.size >= 2) {
      "RemoveTorrentError JSON array must contain at least 2 elements, got ${array.size}"
    }

    return RemoveTorrentError(
      id = array[0].jsonPrimitive.content,
      message = array[1].jsonPrimitive.content,
    )
  }

  override fun serialize(encoder: Encoder, value: RemoveTorrentError) {
    val jsonEncoder = encoder as? JsonEncoder ?: error("RemoveTorrentErrorSerializer can only be used with JSON encoding")
    val array = buildJsonArray {
      add(value.id.toJsonElement())
      add(value.message.toJsonElement())
    }

    jsonEncoder.encodeJsonElement(array)
  }
}
