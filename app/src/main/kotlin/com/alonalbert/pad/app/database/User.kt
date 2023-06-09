package com.alonalbert.pad.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class User(
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
