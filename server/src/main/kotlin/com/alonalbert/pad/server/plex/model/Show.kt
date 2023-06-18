package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Show(
    val key: String = "",
    val title: String = "",
)