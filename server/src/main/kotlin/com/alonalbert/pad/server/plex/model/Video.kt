package com.alonalbert.pad.server.plex.model

import java.time.Instant

interface Video {
  val key: String
  val name: String
  val lastViewed: Instant
}
