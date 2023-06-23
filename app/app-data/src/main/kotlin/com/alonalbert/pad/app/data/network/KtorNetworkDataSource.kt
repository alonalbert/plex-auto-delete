package com.alonalbert.pad.app.data.network

import android.app.Application
import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.mapping.ExternalToNetwork.toNetwork
import com.alonalbert.pad.app.data.mapping.NetworkToExternal.toExternal
import com.alonalbert.pad.app.data.settings.PASSWORD
import com.alonalbert.pad.app.data.settings.SERVER
import com.alonalbert.pad.app.data.settings.USERNAME
import com.alonalbert.pad.app.data.settings.getSetting
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.alonalbert.pad.model.AutoDeleteResult as NetworkAutoDeleteResult
import com.alonalbert.pad.model.AutoWatchResult as NetworkAutoWatchResult
import com.alonalbert.pad.model.Show as NetworkShow
import com.alonalbert.pad.model.User as NetworkUser


internal class KtorNetworkDataSource @Inject constructor(
    private val application: Application,
) : NetworkDataSource {
    override suspend fun loadUsers() = get<List<NetworkUser>>("users").toExternal()

    override suspend fun updateUser(user: User) = put<NetworkUser>("users/${user.id}", user.toNetwork()).toExternal()

    override suspend fun loadShows() = get<List<NetworkShow>>("shows").toExternal()

    private fun httpClient() = HttpClient(Android) {
        install(Logging) {
            logger = TimberLogger
            this.level = INFO
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
        }
        install(Auth) {
            basic {
                credentials {
                    // TODO: Password settings
                    BasicAuthCredentials(application.getSetting(USERNAME) ?: "", application.getSetting(PASSWORD) ?: "")
                }
            }
        }

    }

    override suspend fun runAutoWatch(): AutoWatchResult = get<NetworkAutoWatchResult>("action/auto-watch").toExternal()

    override suspend fun runAutoDelete(days: Int, isTestMode: Boolean): AutoDeleteResult =
        get<NetworkAutoDeleteResult>("action/auto-delete?days=$days&isTestMode=$isTestMode").toExternal()

    private suspend inline fun <reified T> get(url: String): T {
        return httpClient().use {
            withContext(Dispatchers.IO) {
                it.get(getUrl(url)).body()
            }
        }
    }

    private suspend inline fun <reified T> put(url: String, value: T): T {
        return httpClient().use {
            withContext(Dispatchers.IO) {
                it.put(getUrl(url)) {
                    contentType(ContentType.Application.Json)
                    setBody(value)
                }.body()
            }
        }
    }

    private object TimberLogger : Logger {
        override fun log(message: String) {
            Timber.tag("PAD-HTTP").v(message)
        }
    }

    private suspend fun getUrl(segment: String) = "http://${application.getSetting(SERVER)}/api/$segment"
}