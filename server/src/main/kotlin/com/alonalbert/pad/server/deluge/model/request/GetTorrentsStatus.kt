package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.Torrent

class GetTorrentsStatus(
  filters: Map<String, String> = emptyMap(),
  keys: List<String> = emptyList()
) : Request<GetTorrentsStatus, Map<String, Torrent>>("core.get_torrents_status", filters, keys)