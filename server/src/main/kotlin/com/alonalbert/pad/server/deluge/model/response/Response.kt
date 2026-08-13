package com.alonalbert.pad.server.deluge.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
  val result: T? = null,
  val error: Error? = null,
  val id: Int? = null
)
