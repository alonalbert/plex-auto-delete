package com.alonalbert.pad.server.plex.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlexMedia(
    @SerialName("Part")
    val parts: List<PlexPart> = emptyList(),
)