package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.Torrent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class GetTorrentsStatus(
  @Transient val filters: Map<String, String> = emptyMap(),
  @Transient val keys: List<String> = emptyList()
) : Request<GetTorrentsStatus, Map<String, Torrent>>("core.get_torrents_status", filters, keys)