package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_show",
    primaryKeys = ["userId", "showId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalUser::class,
            childColumns = ["userId"],
            parentColumns = ["id"]
        ),
        ForeignKey(
            entity = LocalShow::class,
            childColumns = ["showId"],
            parentColumns = ["id"]
        )
    ],
)
data class LocalUserShow(
    val userId: Long = 0,
    val showId: Long = 0,
)
