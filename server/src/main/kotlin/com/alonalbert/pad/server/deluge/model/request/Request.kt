package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import java.util.concurrent.atomic.AtomicInteger

private val ID = AtomicInteger(0)

abstract class Request<A, D>(
  val method: String = "",
  val params: List<JsonElement>,
  val id: Int,
  ) {
  constructor(method: String, vararg params: Any): this(method, params.map { it.toJsonElement() }, ID.incrementAndGet())

  fun toJson() = buildJsonObject {
    put("method", method.toJsonElement())
    put("params", params.toJsonElement())
    put("id", id.toJsonElement())
  }
}
