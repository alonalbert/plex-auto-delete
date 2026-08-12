package com.alonalbert.pad.server.deluge

import com.alonalbert.pad.server.config.getDelugePassword
import com.alonalbert.pad.server.config.getDelugeUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URI
import java.util.Properties

@Component
class DelugeClient(url: String, private val password: String) {

  @Autowired
  constructor(environment: Environment) : this(environment.getDelugeUrl(), environment.getDelugePassword())

  private val parsedUri = URI.create(url)
  private val basicAuthCredentials = parsedUri.userInfo?.let { userInfo ->
    val parts = userInfo.split(":", limit = 2)
    if (parts.size == 2) BasicAuthCredentials(parts[0], parts[1]) else null
  }

  suspend fun getTorrents(label: String): List<Torrent> {
    val client = httpClient()
    return client.use { httpClient ->
      val jsonUrl = uri("/json").toASCIIString()

      // 1. Authenticate with auth.login
      val loginBody = buildJsonRpcRequest(
        method = "auth.login",
        params = buildJsonArray {
          add(password)
        },
        id = 1
      )
      val loginResponse = httpClient.post(jsonUrl) {
        contentType(ContentType.Application.Json)
        setBody(loginBody)
      }.body<JsonRpcResponse>()

      if (loginResponse.error != null || loginResponse.result?.jsonPrimitive?.booleanOrNull != true) {
        throw IllegalStateException("Failed to authenticate with Deluge Web UI: ${loginResponse.error}")
      }

      // 2. Ensure connected to Deluge daemon
      val connectedBody = buildJsonRpcRequest("web.connected", buildJsonArray {}, id = 2)
      val connectedResponse = httpClient.post(jsonUrl) {
        contentType(ContentType.Application.Json)
        setBody(connectedBody)
      }.body<JsonRpcResponse>()

      val isConnected = connectedResponse.result?.jsonPrimitive?.booleanOrNull == true
      if (!isConnected) {
        val getHostsBody = buildJsonRpcRequest("web.get_hosts", buildJsonArray {}, id = 3)
        val hostsResponse = httpClient.post(jsonUrl) {
          contentType(ContentType.Application.Json)
          setBody(getHostsBody)
        }.body<JsonRpcResponse>()

        val hosts = hostsResponse.result as? JsonArray
        val hostId = hosts?.firstOrNull()?.jsonArray?.getOrNull(0)?.jsonPrimitive?.content
        if (hostId != null) {
          val connectBody = buildJsonRpcRequest("web.connect", buildJsonArray { add(hostId) }, id = 4)
          httpClient.post(jsonUrl) {
            contentType(ContentType.Application.Json)
            setBody(connectBody)
          }
        }
      }

      // 3. Query torrents with core.get_torrents_status
      val keys = buildJsonArray {
        add("name")
        add("label")
        add("save_path")
        add("state")
        add("progress")
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

      val torrentsRpcBody = buildJsonRpcRequest(
        method = "core.get_torrents_status",
        params = filterDict,
        id = 5
      )

      val torrentsResponse = httpClient.post(jsonUrl) {
        contentType(ContentType.Application.Json)
        setBody(torrentsRpcBody)
      }.body<JsonRpcResponse>()

      if (torrentsResponse.error != null) {
        throw IllegalStateException("Failed to fetch torrents from Deluge: ${torrentsResponse.error}")
      }

      parseTorrents(torrentsResponse.result)
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

      Torrent(
        id = id,
        name = name,
        label = label,
        savePath = savePath,
        state = state,
        progress = progress,
      )
    }
  }

  private fun buildJsonRpcRequest(method: String, params: JsonArray, id: Int): JsonRpcRequest {
    return JsonRpcRequest(
      method = method,
      params = params,
      id = id
    )
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

  private fun httpClient() = HttpClient(Apache) {
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
          sendWithoutRequest {
            true
          }
          credentials {
            credentials
          }
        }
      }
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
}

fun main(): Unit = runBlocking {
  val properties = Properties().apply {
    DelugeClient::class.java.classLoader.getResourceAsStream("local.properties")?.use {
      load(it)
    } ?: error("Could not find local.properties in classpath")
  }
  val client = DelugeClient(properties.getProperty("deluge.url"), properties.getProperty("deluge.password"))
  client.getTorrents("tv-sonarr").forEach {
    println(it)
  }
}