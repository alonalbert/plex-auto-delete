package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show")
data class LocalShow(
    @PrimaryKey val id: Long = 0,
    val name: String = "",
)
