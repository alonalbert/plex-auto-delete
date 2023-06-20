package com.alonalbert.pad.server.plex.model

import kotlinx.serialization.Serializable

@Serializable
data class PlexShow(
    val key: String = "",
    val ratingKey: String = "",
    val title: String = "",
)