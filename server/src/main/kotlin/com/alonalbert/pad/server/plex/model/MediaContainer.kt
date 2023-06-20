package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class MediaContainer<T>(
    @JsonProperty("Directory")
    @SerialName("Directory")
    val directories: List<T>? = null,
    @JsonProperty("Metadata")
    @SerialName("Metadata")
    val metadataItems: List<T>? = null,
)