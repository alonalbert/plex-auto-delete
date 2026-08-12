package com.alonalbert.pad.server.deluge

import kotlin.time.Duration

data class Torrent(
  val id: String,
  val name: String,
  val label: String,
  val savePath: String,
  val state: String,
  val progress: Double,
  val activeTime: Duration,
  val seedingTime: Duration,
)
