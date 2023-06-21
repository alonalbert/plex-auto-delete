package com.alonalbert.pad.model

import kotlinx.serialization.Serializable

@Serializable
data class AutoDeleteResult(
    val numFiles: Int = 0,
    val numBytes: Long = 0,
    val shows: Set<String> = emptySet(),
)