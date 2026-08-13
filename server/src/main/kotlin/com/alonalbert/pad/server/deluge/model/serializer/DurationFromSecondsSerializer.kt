package com.alonalbert.pad.server.deluge.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DurationFromSecondsSerializer: KSerializer<Duration> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DurationFromSeconds", PrimitiveKind.LONG)
  override fun deserialize(decoder: Decoder): Duration = decoder.decodeLong().seconds

  override fun serialize(encoder: Encoder, value: Duration) {
    encoder.encodeLong(value.inWholeSeconds)
  }
}