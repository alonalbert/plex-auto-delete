package com.alonalbert.pad.app.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkUser(
    val id: Long = 0,
    val name: String = "",
    val plexToken: String? = null,
    val type: UserType = UserType.INCLUDE
) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }
}

