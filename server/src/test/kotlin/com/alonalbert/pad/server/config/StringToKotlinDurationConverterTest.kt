package com.alonalbert.pad.server.config

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class StringToKotlinDurationConverterTest {

  private val converter = StringToKotlinDurationConverter()

  @Test
  fun testConvertDays() {
    assertEquals(3.days, converter.convert("3d"))
  }

  @Test
  fun testConvertHours() {
    assertEquals(12.hours, converter.convert("12h"))
  }

  @Test
  fun testConvertMinutes() {
    assertEquals(45.minutes, converter.convert("45m"))
  }
}
