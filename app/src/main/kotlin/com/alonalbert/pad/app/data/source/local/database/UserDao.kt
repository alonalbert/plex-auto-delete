package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY name")
    fun observeAll(): Flow<List<LocalUser>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun observe(id: Long): Flow<LocalUserWithShows>

    @Update
    suspend fun update(user: LocalUser)

    @Upsert
    suspend fun upsertAll(users: List<LocalUser>)
}