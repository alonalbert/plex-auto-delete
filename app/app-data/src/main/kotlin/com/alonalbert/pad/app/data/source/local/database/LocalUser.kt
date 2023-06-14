package com.alonalbert.pad.app.data.source.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class LocalUser(
    @PrimaryKey val id: Long = 0,
    val name: String = "",
    val plexToken: String? = null,
    val type: UserType = UserType.INCLUDE
) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }
}

