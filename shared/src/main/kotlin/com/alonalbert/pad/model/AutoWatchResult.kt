package com.alonalbert.pad.model

import kotlinx.serialization.Serializable

@Serializable
data class AutoWatchResult(
    val users: List<User> = emptyList(),
)