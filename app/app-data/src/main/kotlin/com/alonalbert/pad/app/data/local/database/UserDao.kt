package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal interface UserDao {
    @Query("SELECT * FROM user ORDER BY name")
    fun observeAllUsersWithShow(): Flow<List<LocalUserWithShows>>

    fun observeAll(): Flow<List<LocalUser>> = observeAllUsersWithShow().map { list -> list.map { it.toLocalUser() } }

    @Query("SELECT * FROM user WHERE id = :id")
    fun observe(id: Long): Flow<LocalUserWithShows>

    @Update
    suspend fun update(user: LocalUser)

    @Upsert
    suspend fun upsertAll(users: List<LocalUser>)

    @Query("DELETE  FROM user")
    suspend fun deleteAll()
}

private fun LocalUserWithShows.toLocalUser() = user.copy(shows = shows)
