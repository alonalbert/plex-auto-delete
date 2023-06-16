package com.alonalbert.pad.model

import kotlinx.serialization.Serializable

@Serializable
data class UserShow(
    val userId: Long = 0,
    val showId: Long = 0,
)