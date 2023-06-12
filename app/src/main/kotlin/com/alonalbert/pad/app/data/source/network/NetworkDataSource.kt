package com.alonalbert.pad.app.data.source.network

/**
 * Main entry point for accessing data from the network.
 */
interface NetworkDataSource {
    suspend fun loadUsers(): List<NetworkUser>

    suspend fun updateUser(user: NetworkUser): NetworkUser

    suspend fun loadShows(): List<NetworkShow>
}