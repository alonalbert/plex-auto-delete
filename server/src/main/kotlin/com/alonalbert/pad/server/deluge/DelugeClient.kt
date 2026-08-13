package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.config.getDelugePassword
import com.alonalbert.pad.server.config.getDelugeUrl
import com.alonalbert.pad.server.config.getDelugeUsername
import com.alonalbert.pad.server.config.getDelugeWebPassword
import com.alonalbert.pad.server.deluge.model.request.GetTorrentsStatus
import com.alonalbert.pad.server.deluge.model.request.Login
import com.alonalbert.pad.server.deluge.model.request.Request
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.Properties
import java.util.concurrent.atomic.AtomicInteger

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

  suspend fun login() {
    call(Login(webPassword))
  }

  val DelugeAuthPlugin = createClientPlugin("DelugeAuth", {}) {
    val authMutex = Mutex()
    val requestId = AtomicInteger(1000)
    var isAuthenticated = false

    on(Send) { request ->
      val isAuthRequest = request.attributes.computeIfAbsent(IsAuthRequestKey) { false }

      suspend fun performLogin() {
        // 1. Authenticate with auth.login
        val loginBody = JsonRpcRequest(
          method = "auth.login",
          params = buildJsonArray { add(webPassword) },
          id = requestId.getAndIncrement()
        )
        val loginCall = client.post("$url/json") {
          attributes.put(IsAuthRequestKey, true)
          contentType(Application.Json)
          setBody(loginBody)
        }
        val loginResponse = loginCall.body<JsonRpcResponse>()
        if (loginResponse.error != null || loginResponse.result?.jsonPrimitive?.booleanOrNull != true) {
          throw IllegalStateException("Failed to authenticate with Deluge Web UI: ${loginResponse.error}")
        }

        // 2. Ensure connected to Deluge daemon
        val connectedBody = JsonRpcRequest("web.connected", buildJsonArray {}, requestId.getAndIncrement())
        val connectedCall = client.post("$url/json") {
          attributes.put(IsAuthRequestKey, true)
          contentType(Application.Json)
          setBody(connectedBody)
        }
        val connectedResponse = connectedCall.body<JsonRpcResponse>()

        val isConnected = connectedResponse.result?.jsonPrimitive?.booleanOrNull == true
        if (!isConnected) {
          val getHostsBody = JsonRpcRequest("web.get_hosts", buildJsonArray {}, requestId.getAndIncrement())
          val hostsCall = client.post("$url/json") {
            attributes.put(IsAuthRequestKey, true)
            contentType(Application.Json)
            setBody(getHostsBody)
          }
          val hostsResponse = hostsCall.body<JsonRpcResponse>()

          val hosts = hostsResponse.result as? JsonArray
          val hostId = hosts?.firstOrNull()?.jsonArray?.getOrNull(0)?.jsonPrimitive?.content
          if (hostId != null) {
            val connectBody = JsonRpcRequest("web.connect", buildJsonArray { add(hostId) }, requestId.getAndIncrement())
            client.post("$url/json") {
              attributes.put(IsAuthRequestKey, true)
              contentType(Application.Json)
              setBody(connectBody)
            }
          }
        }
        isAuthenticated = true
      }

      if (!isAuthRequest && !isAuthenticated) {
        authMutex.withLock {
          if (!isAuthenticated) {
            performLogin()
          }
        }
      }

      proceed(request)
    }
  }

  suspend fun getTorrents(label: String): Map<String, Torrent> =
    call(GetTorrentsStatus(mapOf("label" to label), listOf("name", "seeding_time")))

  private suspend inline fun <reified A : Any, reified T> call(request: Request<A, T>): T {
    val response = httpClient.post("$url/json") {
      contentType(Application.Json)
      setBody(request as A)
    }
    val body = try {
      response.body<Response<T>>()
    } catch (e: Exception) {
      throw RuntimeException("Invalid response\n${response.bodyAsText()}", e)
    }
    return body.result ?: throw Exception(body.error?.message)
  }

  private val httpClient = createClient()

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


      install(DelugeAuthPlugin)
    }

//    httpClient.plugin(HttpSend).intercept { request ->
//      execute(request)
//    }
    return httpClient
  }

  override fun close() {
    httpClient.close()
  }
}

@Serializable
private data class JsonRpcRequest(
  val method: String,
  val params: JsonArray,
  val id: Int
)

@Serializable
private data class JsonRpcResponse(
  val result: JsonElement? = null,
  val error: JsonElement? = null,
  val id: Int? = null
)

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
    client.getTorrents("tv-sonarr").forEach {
      println(it)
    }
  }
}
