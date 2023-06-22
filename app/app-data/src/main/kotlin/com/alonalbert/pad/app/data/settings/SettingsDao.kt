package com.alonalbert.pad.app.data.settings

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT CAST (value AS INTEGER) FROM settings  WHERE name = :name")
    fun observeInt(name: String): Flow<Int>

    @Query("SELECT value FROM settings WHERE name = :name")
    fun observeString(name: String): Flow<String>

    @Query("INSERT OR REPLACE INTO settings (name, value) VALUES (:name, :value)")
    fun setValue(name: String, value: String)
}
