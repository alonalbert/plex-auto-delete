package com.alonalbert.pad.server.plex

import com.alonalbert.pad.server.plex.model.PlexData
import com.alonalbert.pad.server.plex.model.Section
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers


class PlexClient(private val plexUrl: String, private val userToken: String = "") {
    private val client = HttpClient.newHttpClient()
    private val objectMapper = ObjectMapper()

    fun getSections() = getItems<Section>("/library/sections")

    private inline fun <reified T> getItems(path: String): List<T> {
        val request = HttpRequest.newBuilder().uri(URI.create("$plexUrl$path"))
            .GET()
            .header("Accept", "application/json")
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val body = response.body()
        val ref: TypeReference<PlexData<T>> = object : TypeReference<PlexData<T>>() {}

        val plexData = objectMapper.readValue(body, ref)

        return plexData.mediaContainer.directories
    }
}

fun main() {
    val plexClient = PlexClient("http://10.0.0.2:32400", "")
    plexClient.getSections().forEach {
        println(it)
    }
}