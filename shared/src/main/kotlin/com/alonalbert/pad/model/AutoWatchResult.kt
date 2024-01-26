package com.alonalbert.pad.model

import kotlinx.serialization.Serializable

@Serializable
data class AutoWatchResult(
  val users: Map<String, List<String>>,
)