package com.alonalbert.pad.app.data.source.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkShow(
    val id: Long = 0,
    val name: String = "",
)
