package com.alonalbert.pad.server.plex

import com.alonalbert.pad.server.config.getPlexUrl
import com.alonalbert.pad.server.plex.model.PlexData
import com.alonalbert.pad.server.plex.model.PlexEpisode
import com.alonalbert.pad.server.plex.model.PlexSection
import com.alonalbert.pad.server.plex.model.PlexShow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import java.net.URI


@Component
class PlexClient(environment: Environment) {
    private val plexUrl = environment.getPlexUrl()

    suspend fun getTvSections(userToken: String = "") =
        getItems<PlexSection>("/library/sections", userToken).filter { it.type == "show" }

    suspend fun getUnwatchedShows(sectionKey: String, userToken: String = "") =
        getItems<PlexShow>("/library/sections/$sectionKey/unwatched", userToken)

    suspend fun getAllShows(sectionKey: String, userToken: String = "") =
        getItems<PlexShow>("/library/sections/$sectionKey/all", userToken)

    suspend fun getEpisodes(showKey: String, userToken: String = ""): List<PlexEpisode> =
        getItems<PlexEpisode>("library/metadata/$showKey/allLeaves", userToken)

    fun markShowWatched(plexShow: PlexShow, userToken: String = "") {
        val url = createUriBuilder(plexUrl, userToken)
            .pathSegment(":", "scrobble")
            .queryParam("key", plexShow.ratingKey)
            .queryParam("identifier", "com.plexapp.plugins.library")
            .build()
            .toURL()
        httpClient().use {
            runBlocking {
                it.get(url) {
                    header("Accept", "application/json")
                }
            }
        }
    }

    fun markShowUnwatched(plexShow: PlexShow, userToken: String = "") {
        val url = createUriBuilder(plexUrl, userToken)
            .pathSegment(":", "unscrobble")
            .queryParam("key", plexShow.ratingKey)
            .queryParam("identifier", "com.plexapp.plugins.library")
            .build()
            .toURL()
        httpClient().use {
            runBlocking {
                it.get(url) {
                    header("Accept", "application/json")
                }
            }
        }
    }

    private fun httpClient() = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
        }
    }

    private suspend inline fun <reified T> getItems(path: String, userToken: String): List<T> {
        val url = createUriBuilder(plexUrl, userToken).pathSegment(*path.split("/").toTypedArray()).build().toURL()
        return runBlocking {
            httpClient().use {
                val response = it.get(url) {
                    header("Accept", "application/json")
                }
                val mediaContainer = response.body<PlexData<T>>().mediaContainer
                mediaContainer.directories ?: mediaContainer.metadataItems ?: emptyList()
            }
        }
    }

    private fun createUriBuilder(plexUrl: String, userToken: String): UriBuilder {
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
