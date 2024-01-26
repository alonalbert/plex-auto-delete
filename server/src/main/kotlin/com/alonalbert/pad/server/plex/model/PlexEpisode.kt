package com.alonalbert.pad.server.plex.model

import com.alonalbert.pad.util.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_FUTURE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlexEpisode(
  val key: String = "",

  @SerialName("grandparentTitle")
  val showTitle: String = "",

  val viewCount: Int = 0,

  @Serializable(with = InstantSerializer::class)
  val lastViewedAt: Instant = DISTANT_FUTURE,

  @SerialName("Media")
  val medias: List<PlexMedia> = emptyList(),
)