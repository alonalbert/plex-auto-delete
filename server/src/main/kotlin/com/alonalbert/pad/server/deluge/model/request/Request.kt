package com.alonalbert.pad.server.deluge.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import java.util.concurrent.atomic.AtomicInteger

private val ID = AtomicInteger(0)

@Serializable
abstract class Request<A, D>(
  val method: String = "",
  val params: List<JsonElement>,
  val id: Int,
  ) {
  constructor(method: String, vararg params: Any): this(method, params.map { it.toJsonElement() }, ID.incrementAndGet())
}

private fun Any?.toJsonElement(): JsonElement = when (this) {
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