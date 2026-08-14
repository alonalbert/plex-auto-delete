package com.alonalbert.pad.server.deluge.model.config

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.bind.Name
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

data class LabelConfig(
  val name: String,
  @Name("age")
  @JsonProperty("age")
  val javaAge: JavaDuration,
  val removeData: Boolean,
  val maxTorrents: Int? = null,
) {
  val age: Duration get() = javaAge.toKotlinDuration()
}
