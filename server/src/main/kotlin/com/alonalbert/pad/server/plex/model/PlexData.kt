package com.alonalbert.pad.server.plex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlexData<T>(
  @SerialName("MediaContainer")
  var mediaContainer: PlexMediaContainer<T> = PlexMediaContainer(),
)