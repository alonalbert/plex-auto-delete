package com.alonalbert.pad.app.data

data class AutoDeleteResult(
    val numFiles: Int = 0,
    val numBytes: Long = 0,
    val shows: Set<String> = emptySet(),
)