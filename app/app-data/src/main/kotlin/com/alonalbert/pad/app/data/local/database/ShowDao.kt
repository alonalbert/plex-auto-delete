package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ShowDao {
  @Query("SELECT * FROM show ORDER BY name COLLATE NOCASE")
  fun observeAll(): Flow<List<LocalShow>>

  @Upsert
  suspend fun upsertAll(shows: List<LocalShow>)

  @Query("DELETE FROM show WHERE id NOT IN (:ids)")
  suspend fun deleteExcept(ids: Collection<Long>)

  @Transaction
  suspend fun refreshAll(shows: List<LocalShow>) {
    deleteExcept(shows.map { it.id })
    upsertAll(shows)
  }
}
