package com.alonalbert.pad.util

import java.text.StringCharacterIterator


/**
 * Based on https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
 */
fun Long.toByteUnitString(): String {
  var value = this
  if (value < 0) {
    throw IllegalArgumentException("Must be positive")
  }
  if (value < 1024) {
    return "$this B"
  }
  @Suppress("SpellCheckingInspection")
  val iterator = StringCharacterIterator("KMGTPE")
  var i = 40
  while (i >= 0 && this > 0xfffccccccccccccL shr i) {
    value = value shr 10
    iterator.next()
    i -= 10
  }
  return "%.1f %cB".format(value / 1024.0, iterator.current())
}
