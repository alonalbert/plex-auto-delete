package com.alonalbert.pad.app.data.local.database

import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserShow
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getUsersFlow(): Flow<List<User>>

    fun getUserFlow(id: Long): Flow<User>

    suspend fun updateUser(user: User)

    suspend fun refreshUsers(users: List<User>)

    suspend fun refreshShows(shows: List<Show>)

    fun getShowsFlow(): Flow<List<Show>>

    suspend fun updateUserShows(userShows: List<UserShow>)
}