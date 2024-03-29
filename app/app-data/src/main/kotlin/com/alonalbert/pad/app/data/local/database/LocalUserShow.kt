package com.alonalbert.pad.app.data.local.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

@Entity(
  tableName = "user_show",
  primaryKeys = ["userId", "showId"],
  indices = [
    Index(value = ["showId"]),
    Index(value = ["userId"]),
  ],
  foreignKeys = [
    ForeignKey(
      entity = LocalUser::class,
      childColumns = ["userId"],
      parentColumns = ["id"],
      onDelete = CASCADE
    ),
    ForeignKey(
      entity = LocalShow::class,
      childColumns = ["showId"],
      parentColumns = ["id"],
      onDelete = CASCADE,
    )
  ],
)
internal data class LocalUserShow(
  val userId: Long = 0,
  val showId: Long = 0,
)
