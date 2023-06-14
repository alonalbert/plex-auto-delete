package com.alonalbert.pad.app.data.source.local.database

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserWithShows
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getUsersFlow(): Flow<List<User>>

    fun getUserFlow(id: Long): Flow<UserWithShows>

    suspend fun updateUser(user: User)

    suspend fun refreshUsers(users: List<User>)

    suspend fun refreshShows(shows: List<Show>)
    fun getShowsFlow(): Flow<List<Show>>
}