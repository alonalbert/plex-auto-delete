package com.alonalbert.pad.server.config

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.convert.DurationStyle
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

@Component
@ConfigurationPropertiesBinding
class StringToKotlinDurationConverter : Converter<String, Duration> {
  override fun convert(source: String): Duration {
    return DurationStyle.detectAndParse(source).toKotlinDuration()
  }
}
