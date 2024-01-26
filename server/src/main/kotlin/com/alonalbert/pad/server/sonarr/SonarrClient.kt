package com.alonalbert.pad.server.sonarr

import com.alonalbert.pad.server.config.getSonarrApiKey
import com.alonalbert.pad.server.config.getSonarrPassword
import com.alonalbert.pad.server.config.getSonarrUrl
import com.alonalbert.pad.server.config.getSonarrUsername
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI

@Component
class SonarrClient(private val environment: Environment) {
  suspend fun getShows(): List<String> = get<List<SonarrShow>>("/api/v3/series").map { it.title }

  private suspend inline fun <reified T> get(path: String): T {
    return httpClient().use { client ->
      client.get(uri(path).toURL()) {
        header("X-Api-Key", environment.getSonarrApiKey())
      }.body<T>()
    }
  }

  @Suppress("SameParameterValue")
  private fun uri(path: String): URI {
    val url = URI.create(environment.getSonarrUrl())
    return DefaultUriBuilderFactory().builder()
      .scheme(url.scheme)
      .host(url.host)
      .port(url.port)
      .path(path)
      .build()
  }

  private fun httpClient() = HttpClient(Apache) {
    install(ContentNegotiation) {
      json(Json {
        ignoreUnknownKeys = true
      })
    }
    install(Auth) {
      basic {
        sendWithoutRequest {
          true
        }
        credentials {
          BasicAuthCredentials(environment.getSonarrUsername(), environment.getSonarrPassword())
        }
      }
    }
  }

  @Serializable
  private data class SonarrShow(val title: String)
}