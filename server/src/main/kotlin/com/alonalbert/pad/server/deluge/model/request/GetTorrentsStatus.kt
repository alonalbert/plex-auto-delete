package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.Torrent

class GetTorrentsStatus(labels: Set<String>) :
  Request<GetTorrentsStatus, Map<String, Torrent>>(
    "core.get_torrents_status",
    mapOf("label" to labels),
    listOf("name", "label", "seeding_time"),
  )