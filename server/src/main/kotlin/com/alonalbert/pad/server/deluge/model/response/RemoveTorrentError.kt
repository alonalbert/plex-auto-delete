package com.alonalbert.pad.server.deluge.model.response

import com.alonalbert.pad.server.deluge.model.serializer.RemoveTorrentErrorSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RemoveTorrentErrorSerializer::class)
data class RemoveTorrentError(
  val id: String,
  val message: String,
)
