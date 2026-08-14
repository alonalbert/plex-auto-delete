package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.config.getDelugePassword
import com.alonalbert.pad.server.config.getDelugeUrl
import com.alonalbert.pad.server.config.getDelugeUsername
import com.alonalbert.pad.server.config.getDelugeWebPassword
import com.alonalbert.pad.server.deluge.model.request.AuthLogin
import com.alonalbert.pad.server.deluge.model.request.GetTorrentsStatus
import com.alonalbert.pad.server.deluge.model.request.RemoveTorrents
import com.alonalbert.pad.server.deluge.model.request.Request
import com.alonalbert.pad.server.deluge.model.request.WebConnect
import com.alonalbert.pad.server.deluge.model.request.WebConnected
import com.alonalbert.pad.server.deluge.model.request.WebGetHosts
import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError
import com.alonalbert.pad.server.deluge.model.response.Response
import com.alonalbert.pad.server.deluge.model.response.Torrent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.Properties
import kotlin.time.Duration.Companion.days

private val IsAuthRequestKey = AttributeKey<Boolean>("DelugeIsAuthRequest")

@Component
class DelugeClient(
  private val url: String,
  private val username: String,
  private val password: String,
  private val webPassword: String,
) : AutoCloseable {

  @Autowired
  constructor(environment: Environment) : this(
    environment.getDelugeUrl(),
    environment.getDelugeUsername(),
    environment.getDelugePassword(),
    environment.getDelugeWebPassword(),
  )
  private val client = createClient()
  private val authMutex = Mutex()
  private var isAuthenticated = false

  suspend fun getTorrents(labels: Collection<String>): List<Torrent> =
    call(GetTorrentsStatus(labels))

  suspend fun removeTorrents(
    ids: Collection<String>,
    removeData: Boolean = false,
  ): List<RemoveTorrentError> {
    if (ids.isEmpty()) return emptyList()
    return call(RemoveTorrents(ids, removeData))
  }

  override fun close() {
    client.close()
  }

  private suspend inline fun <reified A : Request<A, T>, reified T> call(
    request: Request<A, T>,
    block: HttpRequestBuilder.() -> Unit = {}
  ): T {
    val response = client.post("$url/json") {
      contentType(Application.Json)
      setBody(request.toJson())
      block()
    }
    val body = try {
      response.body<Response<T>>()
    } catch (e: Exception) {
      throw RuntimeException("Invalid response\n${response.bodyAsText()}", e)
    }
    return body.result ?: throw Exception(body.error?.message)
  }

  private fun createClient(): HttpClient {
    val httpClient = HttpClient(Apache) {
      install(ContentNegotiation) {
        json(Json {
          ignoreUnknownKeys = true
        })
      }
      install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
      }
      install(Auth) {
        basic {
          sendWithoutRequest { true }
          credentials { BasicAuthCredentials(this@DelugeClient.username, this@DelugeClient.password) }
        }
      }

      install(createClientPlugin("DelugeAuth", {}) {
        on(Send) { request ->
          val isAuthRequest = request.attributes.computeIfAbsent(IsAuthRequestKey) { false }
          if (!isAuthRequest && !isAuthenticated) {
            authMutex.withLock {
              if (!isAuthenticated) {
                login()
              }
            }
          }
          proceed(request)
        }
      })
    }
    return httpClient
  }

  private suspend fun login() {
    val markAsAuth: HttpRequestBuilder.() -> Unit = { attributes.put(IsAuthRequestKey, true) }

    if (!call<AuthLogin, Boolean>(AuthLogin(webPassword), markAsAuth)) {
      throw IllegalStateException("Failed to authenticate with Deluge Web UI")
    }
    if (!call<WebConnected, Boolean>(WebConnected(), markAsAuth)) {
      val hosts = call(WebGetHosts(), markAsAuth)
      val hostId = hosts.firstOrNull()?.id ?: throw IllegalStateException("No hosts found")
      call(WebConnect(hostId), markAsAuth)
    }
    isAuthenticated = true
  }
}

fun main(): Unit = runBlocking {
  val properties = Properties().apply {
    DelugeClient::class.java.classLoader.getResourceAsStream("local.properties")?.use {
      load(it)
    } ?: error("Could not find local.properties in classpath")
  }
  DelugeClient(
    properties.getProperty("deluge.url"),
    properties.getProperty("deluge.username"),
    properties.getProperty("deluge.password"),
    properties.getProperty("deluge.web.password"),
  ).use { client ->
    val torrentsToRemove = client.getTorrents(setOf("tv-sonarr")).filter { it.seedingTime > 3.days }
    torrentsToRemove.forEach { torrent ->
      println("Removing torrent ${torrent.name} (${torrent.id}): Seeding for ${torrent.seedingTime}")
    }
//    client.removeTorrents(torrentsToRemove.map { it.id }, removeData = true)
  }
}
