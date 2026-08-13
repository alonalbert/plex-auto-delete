package com.alonalbert.pad.server.deluge.model.response

import com.alonalbert.pad.server.deluge.model.serializer.DurationFromSecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Torrent(
  val name: String,
  @SerialName("seeding_time")
  @Serializable(with = DurationFromSecondsSerializer::class)
  val seedingTime: Duration,
)