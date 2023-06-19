package com.alonalbert.pad.server.plex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlexEpisode(
    @JsonProperty("key") val key: String = "",
    @JsonProperty("grandparentTitle") val showTitle: String = "",
    @JsonProperty("lastViewedAt") val lastViewedAt: Instant? = null,
    @JsonProperty("Media") val medias: List<PlexMedia> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlexMedia(
    @JsonProperty("Part") val parts: List<PlexPart> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlexPart(
    val file: String = "",
)
