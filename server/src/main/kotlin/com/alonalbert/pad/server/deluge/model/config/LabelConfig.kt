package com.alonalbert.pad.server.deluge.model.config

import java.time.Duration
import kotlin.time.toKotlinDuration

data class LabelConfig(val name: String, val javaAge: Duration, val removeData: Boolean) {
  val age = javaAge.toKotlinDuration()
}