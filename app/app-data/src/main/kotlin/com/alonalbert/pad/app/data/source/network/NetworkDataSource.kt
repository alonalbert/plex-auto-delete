package com.alonalbert.pad.app.data.source.network

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User

/**
 * Main entry point for accessing data from the network.
 */
interface NetworkDataSource {
    suspend fun loadUsers(): List<User>

    suspend fun updateUser(user: User): User

    suspend fun loadShows(): List<Show>
}
