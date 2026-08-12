package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.config.getDelugePassword
import com.alonalbert.pad.server.config.getDelugeUrl
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
import io.ktor.http.ContentType
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI
import java.util.Properties
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

private val IsAuthRequestKey = AttributeKey<Boolean>("DelugeIsAuthRequest")

class DelugeAuthConfig {
  var passwordSupplier: () -> String = { "" }
  var jsonUrlSupplier: () -> String = { "" }
}

val DelugeAuthPlugin = createClientPlugin("DelugeAuth", ::DelugeAuthConfig) {
  val passwordSupplier = pluginConfig.passwordSupplier
  val jsonUrlSupplier = pluginConfig.jsonUrlSupplier
  val authMutex = Mutex()
  val requestId = AtomicInteger(1000)
  var isAuthenticated = false

  on(Send) { request ->
    val isAuthRequest = request.attributes.computeIfAbsent(IsAuthRequestKey) { false }
    val jsonUrl = jsonUrlSupplier()

    suspend fun performLogin() {
      val password = passwordSupplier()

      // 1. Authenticate with auth.login
      val loginBody = JsonRpcRequest(
        method = "auth.login",
        params = buildJsonArray { add(password) },
        id = requestId.getAndIncrement()
      )
      val loginCall = client.post(jsonUrl) {
        attributes.put(IsAuthRequestKey, true)
        contentType(ContentType.Application.Json)
        setBody(loginBody)
      }
      val loginResponse = loginCall.body<JsonRpcResponse>()
      if (loginResponse.error != null || loginResponse.result?.jsonPrimitive?.booleanOrNull != true) {
        throw IllegalStateException("Failed to authenticate with Deluge Web UI: ${loginResponse.error}")
      }

      // 2. Ensure connected to Deluge daemon
      val connectedBody = JsonRpcRequest("web.connected", buildJsonArray {}, requestId.getAndIncrement())
      val connectedCall = client.post(jsonUrl) {
        attributes.put(IsAuthRequestKey, true)
        contentType(ContentType.Application.Json)
        setBody(connectedBody)
      }
      val connectedResponse = connectedCall.body<JsonRpcResponse>()

      val isConnected = connectedResponse.result?.jsonPrimitive?.booleanOrNull == true
      if (!isConnected) {
        val getHostsBody = JsonRpcRequest("web.get_hosts", buildJsonArray {}, requestId.getAndIncrement())
        val hostsCall = client.post(jsonUrl) {
          attributes.put(IsAuthRequestKey, true)
          contentType(ContentType.Application.Json)
          setBody(getHostsBody)
        }
        val hostsResponse = hostsCall.body<JsonRpcResponse>()

        val hosts = hostsResponse.result as? JsonArray
        val hostId = hosts?.firstOrNull()?.jsonArray?.getOrNull(0)?.jsonPrimitive?.content
        if (hostId != null) {
          val connectBody = JsonRpcRequest("web.connect", buildJsonArray { add(hostId) }, requestId.getAndIncrement())
          client.post(jsonUrl) {
            attributes.put(IsAuthRequestKey, true)
            contentType(ContentType.Application.Json)
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

@Component
class DelugeClient(url: String, private val password: String) : AutoCloseable {

  @Autowired
  constructor(environment: Environment) : this(environment.getDelugeUrl(), environment.getDelugePassword())

  private val parsedUri = URI.create(url)
  private val jsonUrl = uri("/json").toASCIIString()
  private val basicAuthCredentials = parsedUri.userInfo?.let { userInfo ->
    val parts = userInfo.split(":", limit = 2)
    if (parts.size == 2) BasicAuthCredentials(parts[0], parts[1]) else null
  }

  private val requestId = AtomicInteger(1)

  suspend fun getTorrents(label: String): List<Torrent> {
    val keys = buildJsonArray {
      add("name")
      add("label")
      add("save_path")
      add("state")
      add("progress")
      add("active_time")
      add("seeding_time")
    }
    val filterDict = if (label.isNotBlank()) {
      buildJsonArray {
        addJsonObject {
          put("label", JsonPrimitive(label))
        }
        add(keys)
      }
    } else {
      buildJsonArray {
        addJsonObject {}
        add(keys)
      }
    }

    val rpcBody = JsonRpcRequest("core.get_torrents_status", filterDict, requestId.getAndIncrement())
    val response = httpClient.post(jsonUrl) {
      contentType(ContentType.Application.Json)
      setBody(rpcBody)
    }.body<JsonRpcResponse>()

    if (response.error != null) {
      throw IllegalStateException("RPC call 'core.get_torrents_status' failed: ${response.error}")
    }

    return parseTorrents(response.result)
  }

  private val httpClient = HttpClient(Apache) {
    install(ContentNegotiation) {
      json(Json {
        ignoreUnknownKeys = true
      })
    }
    install(HttpCookies) {
      storage = AcceptAllCookiesStorage()
    }
    basicAuthCredentials?.let { credentials ->
      install(Auth) {
        basic {
          sendWithoutRequest { true }
          credentials { credentials }
        }
      }
    }
    install(DelugeAuthPlugin) {
      passwordSupplier = { this@DelugeClient.password }
      jsonUrlSupplier = { this@DelugeClient.jsonUrl }
    }
  }

  private fun parseTorrents(result: JsonElement?): List<Torrent> {
    val torrentsObj = result as? JsonObject ?: return emptyList()
    return torrentsObj.entries.mapNotNull { (id, value) ->
      val torrentData = value.jsonObject
      val name = torrentData["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
      val label = torrentData["label"]?.jsonPrimitive?.content ?: ""
      val savePath = torrentData["save_path"]?.jsonPrimitive?.content ?: ""
      val state = torrentData["state"]?.jsonPrimitive?.content ?: ""
      val progress = torrentData["progress"]?.jsonPrimitive?.doubleOrNull ?: 0.0
      val activeTimeSeconds = torrentData["active_time"]?.jsonPrimitive?.longOrNull ?: 0L
      val seedingTimeSeconds = torrentData["seeding_time"]?.jsonPrimitive?.longOrNull ?: 0L

      Torrent(
        id = id,
        name = name,
        label = label,
        savePath = savePath,
        state = state,
        progress = progress,
        activeTime = activeTimeSeconds.seconds,
        seedingTime = seedingTimeSeconds.seconds,
      )
    }
  }

  private fun uri(path: String): URI {
    val builder = DefaultUriBuilderFactory().builder()
      .scheme(parsedUri.scheme)
      .host(parsedUri.host)
      .path(path)
    if (parsedUri.port != -1) {
      builder.port(parsedUri.port)
    }
    return builder.build()
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
  DelugeClient(properties.getProperty("deluge.url"), properties.getProperty("deluge.password")).use { client ->
    client.getTorrents("tv-sonarr").forEach {
      println(it)
    }
  }
}