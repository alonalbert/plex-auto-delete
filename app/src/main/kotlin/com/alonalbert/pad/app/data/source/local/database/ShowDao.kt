package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ShowDao {
    @Query("SELECT * FROM show ORDER BY name")
    fun getAll(): List<LocalShow>

    @Upsert
    suspend fun upsertAll(users: List<LocalShow>)
}
