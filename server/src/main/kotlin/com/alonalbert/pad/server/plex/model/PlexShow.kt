package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlexShow(
    val key: String = "",
    val ratingKey: String = "",
    val title: String = "",
)