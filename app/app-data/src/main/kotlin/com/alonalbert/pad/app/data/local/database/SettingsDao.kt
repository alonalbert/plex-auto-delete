package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SettingsDao {
    @Query("SELECT CAST (value AS INTEGER) FROM settings  WHERE name = :name")
    fun observeInt(name: String): Flow<Int>

    @Query("SELECT value FROM settings WHERE name = :name")
    fun observeString(name: String): Flow<String>
}
