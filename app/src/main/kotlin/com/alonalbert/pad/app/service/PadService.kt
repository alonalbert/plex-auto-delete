package com.alonalbert.pad.app.service

import com.alonalbert.pad.app.database.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject

class PadService @Inject constructor() {
    private val server = "http://10.0.0.74:8080/api"
    suspend fun loadUsers(): UsersResult {
        return httpClient().use { client ->
            runCatching { UsersResult.Success(client.get("${server}/users").body<List<User>>()) }.getOrElse { UsersResult.Error(it) }
        }
    }

    suspend fun updateUser(user: User): UpdateUserResult {
        return httpClient().use { client ->
            runCatching {
                val updated = client.put("${server}/users/${user.id}") {
                    contentType(Json)
                    setBody(user)
                }.body<User>()
                UpdateUserResult.Success(updated)
            }.getOrElse { UpdateUserResult.Error(it) }
        }
    }

    private fun httpClient() = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }
}