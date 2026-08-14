package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.Torrent
import com.alonalbert.pad.server.deluge.model.response.Torrents
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TorrentListSerializer : KSerializer<Torrents> {
  private val delegate = MapToPojoListSerializer(Torrent.serializer())

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun deserialize(decoder: Decoder): Torrents {
    return Torrents(delegate.deserialize(decoder))
  }

  override fun serialize(encoder: Encoder, value: Torrents) {
    delegate.serialize(encoder, value)
  }
}
