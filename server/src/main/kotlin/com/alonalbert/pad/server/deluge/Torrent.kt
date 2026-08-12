package com.alonalbert.pad.server.deluge

data class Torrent(
  val id: String,
  val name: String,
  val label: String,
  val savePath: String,
  val state: String,
  val progress: Double,
)
