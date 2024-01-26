package com.alonalbert.pad.server.plex.model

import kotlinx.serialization.Serializable

@Serializable
data class PlexPart(
  val file: String = "",
)