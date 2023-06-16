package com.alonalbert.pad.app.data.local.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

internal data class LocalUserWithShows(
    @Embedded val user: LocalUser,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            LocalUserShow::class,
            parentColumn = "userId",
            entityColumn = "showId",
        )
    )
    val shows: List<LocalShow>
)