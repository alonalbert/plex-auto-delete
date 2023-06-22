package com.alonalbert.pad.app.data.settings

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Settings::class,
    ], version = 1
)
internal abstract class SettingsDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}
