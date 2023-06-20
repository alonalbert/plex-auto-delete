package com.alonalbert.pad.server.plex

import com.alonalbert.pad.server.plex.model.PlexData
import com.alonalbert.pad.server.plex.model.PlexEpisode
import com.alonalbert.pad.server.plex.model.PlexShow
import com.alonalbert.pad.server.plex.model.Section
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import io.ktor.client.plugins.logging.Logger as KtorLogger
import java.net.http.HttpClient as HttpClient1


class PlexClient(private val plexUrl: String, private val userToken: String = "") {
    private val logger = LoggerFactory.getLogger(PlexClient::class.java)

    private val client = HttpClient1.newHttpClient()
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

    private fun httpClient() = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
        }
    }

    private inline fun <reified T> getItems(path: String): List<T> {
        val url = createUriBuilder(plexUrl).pathSegment(*path.split("/").toTypedArray()).build().toURL()
        return runBlocking {
            httpClient().use {
                withContext(Dispatchers.IO) {
                    val mediaContainer = it.get(url) {
                        header("Accept", "application/json")
                    }.body<PlexData<T>>().mediaContainer
                    mediaContainer.directories ?: mediaContainer.metadataItems ?: emptyList()
                }
            }
        }
    }

    private inline fun <reified T> getItems1(path: String): List<T> {
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

    private class ClientLogger(private val logger: Logger) : KtorLogger {
        override fun log(message: String) {
            logger.log(message)
        }
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
