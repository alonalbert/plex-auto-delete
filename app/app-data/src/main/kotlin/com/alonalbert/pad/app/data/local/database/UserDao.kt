package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface UserDao {
    @Query("SELECT * FROM user ORDER BY name")
    fun observeAll(): Flow<List<LocalUser>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun observe(id: Long): Flow<LocalUserWithShows>

    @Update
    suspend fun update(user: LocalUser)

    @Upsert
    suspend fun upsertAll(users: List<LocalUser>)

    @Query("DELETE  FROM user")
    suspend fun deleteAll()

    @Transaction
    suspend fun refreshAll(users: List<LocalUser>) {
        upsertAll(users)
    }

}