package com.alonalbert.pad.app.data.network

import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import javax.inject.Inject

internal class FakePlexNetworkDataSource @Inject constructor(
    delegate: KtorNetworkDataSource,
) : NetworkDataSource by delegate {
    override suspend fun runAutoWatch() = AutoWatchResult(mapOf("User" to listOf("Show")))

    override suspend fun runAutoDelete() = AutoDeleteResult(numFiles = 10, numBytes = 10_000_000, shows = listOf("Show"))
}