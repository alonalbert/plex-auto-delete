package com.alonalbert.pad.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface UserShowDao {
  @Query("DELETE FROM user_show WHERE userId in (:userIds)")
  suspend fun deleteForUsers(userIds: Collection<Long>)

  @Insert
  suspend fun insertAll(items: List<LocalUserShow>)

  @Transaction
  suspend fun update(items: List<LocalUserShow>) {
    val userIds = items.mapTo(mutableSetOf()) { it.userId }
    deleteForUsers(userIds)
    insertAll(items)
  }
}
