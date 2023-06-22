package com.alonalbert.pad.app.data.settings

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT value FROM settings WHERE name = :name")
    fun observeString(name: String): Flow<String>

    @Query("SELECT value FROM settings WHERE name = :name")
    suspend fun getString(name: String): String

    @Query("INSERT OR REPLACE INTO settings (name, value) VALUES (:name, :value)")
    fun setValue(name: String, value: String)
}
