package com.alonalbert.pad.app.data.source.network

import com.alonalbert.pad.model.Show as NetworkShow
import com.alonalbert.pad.model.User as NetworkUser

/**
 * Main entry point for accessing data from the network.
 */
interface NetworkDataSource {
    suspend fun loadUsers(): List<NetworkUser>

    suspend fun updateUser(user: NetworkUser): NetworkUser

    suspend fun loadShows(): List<NetworkShow>
}
