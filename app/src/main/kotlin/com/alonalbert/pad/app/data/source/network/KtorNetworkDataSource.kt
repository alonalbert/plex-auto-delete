package com.alonalbert.pad.app.data.source.network

import com.alonalbert.pad.model.Show
import com.alonalbert.pad.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel.INFO
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import timber.log.Timber
import javax.inject.Inject

typealias NetworkUser = User
typealias NetworkShow = Show

class KtorNetworkDataSource @Inject constructor() : NetworkDataSource {
    private val server = "http://10.0.0.74:8080/api"

    override suspend fun loadUsers() = get<List<NetworkUser>>("${server}/users")

    override suspend fun updateUser(user: NetworkUser) = put<NetworkUser>("${server}/users/${user.id}", user)

    override suspend fun loadShows() = get<List<NetworkShow>>("${server}/shows")

    private fun httpClient() = HttpClient(Android) {
        install(Logging) {
            logger = TimberLogger
            this.level = INFO
        }
        install(ContentNegotiation) {
            json()
        }
    }

    private suspend inline fun <reified T> get(url: String): T {
        return httpClient().use {
            it.get(url).body()
        }
    }

    private suspend inline fun <reified T> put(url: String, value: T): T {
        return httpClient().use {
            it.put(url) {
                contentType(ContentType.Application.Json)
                setBody(value)
            }.body()
        }
    }

    private object TimberLogger : Logger {
        override fun log(message: String) {
            Timber.tag("PAD-HTTP").v(message)
        }
    }
}