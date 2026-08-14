package com.alonalbert.pad.server.deluge.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

open class MapToPojoListSerializer<T : Any>(
  private val elementSerializer: KSerializer<T>,
  private val idFieldName: String = "id",
) : KSerializer<List<T>> {

  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("MapToPojoList") {
      element(idFieldName, elementSerializer.descriptor)
    }

  override fun deserialize(decoder: Decoder): List<T> {
    val jsonDecoder = (decoder as? JsonDecoder)
      ?: error("MapToPojoListSerializer can only be used with JSON decoding")
    val jsonObject = (jsonDecoder.decodeJsonElement() as? JsonObject)
      ?: error("Expected JsonObject for MapToPojoListSerializer")

    return jsonObject.map { (id, element) ->
      val elementObject = (element as? JsonObject)
        ?: error("Expected JsonObject for element with key $id")
      val jsonWithId = JsonObject(mapOf(idFieldName to JsonPrimitive(id)) + elementObject)
      jsonDecoder.json.decodeFromJsonElement(elementSerializer, jsonWithId)
    }
  }

  override fun serialize(encoder: Encoder, value: List<T>) {
    val jsonEncoder = (encoder as? JsonEncoder)
      ?: error("MapToPojoListSerializer can only be used with JSON encoding")

    val jsonObject = buildJsonObject {
      for (item in value) {
        val elementJson = jsonEncoder.json.encodeToJsonElement(elementSerializer, item).jsonObject
        val id = elementJson[idFieldName]?.jsonPrimitive?.content
          ?: error("Missing field '$idFieldName' in serialized element")
        val elementWithoutId = JsonObject(elementJson - idFieldName)
        put(id, elementWithoutId)
      }
    }

    jsonEncoder.encodeJsonElement(jsonObject)
  }
}
