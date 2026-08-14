@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.alonalbert.pad.server.deluge.model.response

import com.alonalbert.pad.server.deluge.model.serializer.TorrentListSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TorrentListSerializer::class)
data class Torrents(private val torrents: List<Torrent>) : List<Torrent> by torrents
