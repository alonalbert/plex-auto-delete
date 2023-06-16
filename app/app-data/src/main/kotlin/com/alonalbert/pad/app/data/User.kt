package com.alonalbert.pad.app.data

data class User(
    val id: Long = 0,
    val name: String = "",
    val plexToken: String? = null,
    val type: UserType = UserType.INCLUDE,
    val shows: List<Show> = emptyList(),
) {
    enum class UserType {
        EXCLUDE,
        INCLUDE,
    }
}

