package com.alonalbert.pad.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [
    LocalUser::class,
    LocalShow::class,
    LocalUserShow::class,
  ],
  version = 1,
  exportSchema = true,
)
internal abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun showDao(): ShowDao
  abstract fun userShowDao(): UserShowDao
}
