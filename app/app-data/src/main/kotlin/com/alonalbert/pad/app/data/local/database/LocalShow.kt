package com.alonalbert.pad.app.data.local.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "show",
    indices = [Index(value = ["id"])],
)
internal data class LocalShow(
    @PrimaryKey val id: Long = 0,
    val name: String = "",
)
