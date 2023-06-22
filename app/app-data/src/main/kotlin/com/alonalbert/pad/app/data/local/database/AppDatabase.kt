package com.alonalbert.pad.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalUser::class,
        LocalShow::class,
        LocalUserShow::class,
        Settings::class,
    ], version = 1
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun showDao(): ShowDao
    abstract fun userShowDao(): UserShowDao
    abstract fun settingsDao(): SettingsDao
}
