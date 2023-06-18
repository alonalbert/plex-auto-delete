package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlexData<T>(
    @JsonProperty("MediaContainer") var mediaContainer: MediaContainer<T> = MediaContainer(),
)