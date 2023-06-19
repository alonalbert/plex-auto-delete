package com.alonalbert.pad.server.plex.model

import java.time.Instant

data class Episode(
  override val key: String,
  override val name: String,
  override val lastViewed: Instant,
  val seasonNumber: Int,
  val episodeNumber: Int,
  val showName: String,
) : Video
