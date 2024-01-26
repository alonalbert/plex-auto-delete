package com.alonalbert.pad.server.plex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlexMediaContainer<T>(
  @SerialName("Directory")
  val directories: List<T>? = null,

  @SerialName("Metadata")
  val metadataItems: List<T>? = null,
)