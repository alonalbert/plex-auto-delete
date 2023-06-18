package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MediaContainer<T>(
    @JsonProperty("Directory") val directories: List<T>? = null,
    @JsonProperty("Metadata") val metadataItems: List<T>? = null,
)