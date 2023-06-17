package com.alonalbert.pad.app.data.local.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "user")
internal data class LocalUser(
    @PrimaryKey val id: Long = 0,

    val name: String = "",

    val plexToken: String? = null,

    val type: UserType = UserType.INCLUDE,

    @Ignore
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            LocalUserShow::class,
            parentColumn = "userId",
            entityColumn = "showId",
        )
    )
    val shows: List<LocalShow> = emptyList(),
) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }

    @Suppress("unused")
    constructor(
        id: Long,
        name: String,
        plexToken: String?,
        type: UserType,
    ) : this(id, name, plexToken, type, emptyList())
}

