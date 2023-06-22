package com.alonalbert.pad.app.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val name: String,
    val value: String = "",
) {
    companion object {
        const val SERVER: String = "server"
        const val USERNAME: String = "username"
        const val PASSWORD: String = "password"
    }
}
