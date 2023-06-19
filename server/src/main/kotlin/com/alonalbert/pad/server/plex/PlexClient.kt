package com.alonalbert.pad.server.plex

import com.alonalbert.pad.server.plex.model.PlexData
import com.alonalbert.pad.server.plex.model.PlexEpisode
import com.alonalbert.pad.server.plex.model.PlexShow
import com.alonalbert.pad.server.plex.model.Section
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers


class PlexClient(private val plexUrl: String, private val userToken: String = "") {
    private val client = HttpClient.newHttpClient()
    private val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())

    fun getTvSections() = getItems<Section>("/library/sections").filter { it.type == "show" }

    fun getUnwatchedShows(sectionKey: String) = getItems<PlexShow>("/library/sections/$sectionKey/unwatched")

    fun getAllShows(sectionKey: String) = getItems<PlexShow>("/library/sections/$sectionKey/all")

    fun getEpisodes(showKey: String): List<PlexEpisode> = getItems<PlexEpisode>("library/metadata/$showKey/allLeaves")

    fun markShowWatched(plexShow: PlexShow) {
        val uri = createUriBuilder(plexUrl)
            .pathSegment(":", "scrobble")
            .queryParam("key", plexShow.ratingKey)
            .queryParam("identifier", "com.plexapp.plugins.library")
            .build()
        val request = HttpRequest.newBuilder().uri(uri)
            .GET()
            .header("Accept", "application/json")
            .build()
        client.send(request, BodyHandlers.ofString())
    }

    private inline fun <reified T> getItems(path: String): List<T> {
        val uri = createUriBuilder(plexUrl).pathSegment(*path.split("/").toTypedArray()).build()
        val request = HttpRequest.newBuilder().uri(uri)
            .GET()
            .header("Accept", "application/json")
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val body = response.body()
        val ref: TypeReference<PlexData<T>> = object : TypeReference<PlexData<T>>() {}

        val mediaContainer = objectMapper.readValue(body, ref).mediaContainer

        return mediaContainer.directories ?: mediaContainer.metadataItems ?: emptyList()
    }

    private fun createUriBuilder(plexUrl: String): UriBuilder {
        val baseUri = URI.create(plexUrl)
        val builder = DefaultUriBuilderFactory().builder()
            .scheme(baseUri.scheme)
            .host(baseUri.host)
            .port(baseUri.port)
            .path(baseUri.path)
        if (userToken.isNotBlank()) {
            builder.queryParam("X-Plex-Token", userToken)
        }
        return builder
    }
}

fun main() {
    val plexClient = PlexClient("http://10.0.0.2:32400", "fYHZ4_de-yx-_qhCmvHz")
    plexClient.getTvSections().filter { it.title == "TV" }.forEach {
        plexClient.getUnwatchedShows(it.key).forEach {
            println(it)
        }
    }
}
