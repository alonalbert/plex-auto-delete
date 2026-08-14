package com.alonalbert.pad.server.deluge.model.config

import kotlin.time.Duration

data class LabelConfig(
  val name: String,
  val age: Duration,
  val removeData: Boolean,
  val maxTorrents: Int?,
)
