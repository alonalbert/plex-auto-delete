package com.alonalbert.pad.server.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

fun Any?.toJsonElement(): JsonElement = when (this) {
  null -> JsonNull
  is JsonElement -> this
  is Number -> JsonPrimitive(this)
  is Boolean -> JsonPrimitive(this)
  is String -> JsonPrimitive(this)
  is Char -> JsonPrimitive(this.toString())
  is Array<*> -> JsonArray(this.map { it.toJsonElement() })
  is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
  is Map<*, *> -> JsonObject(this.entries.associate { (k, v) ->
    k.toString() to v.toJsonElement()
  })
  else -> runCatching {
    Json.encodeToJsonElement(Json.serializersModule.serializer(this::class.java), this)
  }.getOrElse {
    JsonPrimitive(this.toString())
  }
}

