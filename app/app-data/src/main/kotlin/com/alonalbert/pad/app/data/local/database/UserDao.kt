package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal interface UserDao {
  @Transaction
  @Query("SELECT * FROM user ORDER BY name COLLATE NOCASE")
  fun observeAllUsersWithShow(): Flow<List<LocalUserWithShows>>

  fun observeAll(): Flow<List<LocalUser>> = observeAllUsersWithShow().map { list -> list.map { it.toLocalUser() } }

  @Transaction
  @Query("SELECT * FROM user WHERE id = :id")
  fun observeUserWithShow(id: Long): Flow<LocalUserWithShows>

  fun observe(id: Long): Flow<LocalUser> = observeUserWithShow(id).map { it.copy(shows = it.shows).toLocalUser() }

  @Update
  suspend fun update(user: LocalUser)

  @Upsert
  suspend fun upsertAll(users: List<LocalUser>)

  @Query("DELETE  FROM user WHERE id NOT IN (:ids)")
  suspend fun deleteExcept(ids: Collection<Long>)
}

private fun LocalUserWithShows.toLocalUser() = user.copy(shows = shows)
