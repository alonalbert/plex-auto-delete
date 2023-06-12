package com.alonalbert.pad.app.data

import kotlinx.coroutines.flow.Flow

interface Repository {
    /**
     * Users
     */

    fun getUsersFlow(): Flow<List<User>>

    fun getUserFlow(id: Long): Flow<UserWithShows>

    suspend fun refreshUsers(): List<User>

    suspend fun updateUser(user: User)

    /**
     * Shows
     */

    suspend fun refreshShows(): List<Show>
}

