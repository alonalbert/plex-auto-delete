package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Query("SELECT * FROM show ORDER BY name")
    fun observeAll(): Flow<List<LocalShow>>

    @Query("SELECT * FROM show ORDER BY name")
    fun getAll(): List<LocalShow>

    @Upsert
    suspend fun upsertAll(shows: List<LocalShow>)

    @Query("DELETE  FROM show")
    suspend fun deleteAll()

    @Transaction
    suspend fun refreshAll(shows: List<LocalShow>) {
        deleteAll()
        upsertAll(shows)
    }
}
