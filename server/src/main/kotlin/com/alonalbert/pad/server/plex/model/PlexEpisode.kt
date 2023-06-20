package com.alonalbert.pad.server.plex.model

import com.alonalbert.pad.util.InstantSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_FUTURE
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class PlexEpisode(
    @JsonProperty("key")
    @SerialName("key")
    val key: String = "",
    @JsonProperty("grandparentTitle")
    @SerialName("grandparentTitle")
    val showTitle: String = "",
    @SerialName("lastViewedAt")
    @JsonProperty("lastViewedAt")

    @Serializable(with = InstantSerializer::class)
//    val lastViewedAt: Instant? = null,
    val lastViewedAt: Instant = DISTANT_FUTURE,
    @SerialName("Media")
    @JsonProperty("Media")
    val medias: List<PlexMedia> = emptyList(),
)

fun main() {
    InstantComponentSerializer
    val s = "{\"key\": \"foo\", \"lastViewedAt\": 1234567 }"
    val episode = Json.decodeFromString<PlexEpisode>(s)
    println(episode)
}