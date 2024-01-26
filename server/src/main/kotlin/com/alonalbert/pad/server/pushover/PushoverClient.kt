package com.alonalbert.pad.server.pushover

import com.alonalbert.pad.server.config.getPushoverToken
import com.alonalbert.pad.server.config.getPushoverUserToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.net.URL
import java.net.URLEncoder

@Component
class PushoverClient(environment: Environment) {
  private val token = environment.getPushoverToken()
  private val userToken = environment.getPushoverUserToken()

  suspend fun send(message: String) {
    val url = URL("https://api.pushover.net/1/messages.json")
    val postData = "token=$token&user=$userToken&message=${URLEncoder.encode(message, "UTF-8")}"

    httpClient().use {
      it.post(url) {
        setBody(postData)
        header("Content-Type", "application/x-www-form-urlencoded")
        header("Content-Length", postData.length.toString())
      }
    }
  }

  private fun httpClient() = HttpClient(Apache) {
    install(HttpTimeout) {
      requestTimeoutMillis = 10_000
    }
  }
}
