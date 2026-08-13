package com.alonalbert.pad.server.deluge.model.response

import com.alonalbert.pad.server.deluge.model.serializer.HostSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HostSerializer::class)
data class Host(
  val id: String,
  val hostname: String,
  val port: Int,
  val username: String,
  val status: String?,
)
