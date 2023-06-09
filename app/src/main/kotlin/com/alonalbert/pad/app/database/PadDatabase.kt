package com.alonalbert.pad.app.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class PadDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}