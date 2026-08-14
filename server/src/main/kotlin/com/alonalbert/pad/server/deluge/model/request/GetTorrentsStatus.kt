package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.Torrents

class GetTorrentsStatus(labels: Collection<String>) :
  Request<GetTorrentsStatus, Torrents>(
    "core.get_torrents_status",
    mapOf("label" to labels),
    listOf("name", "label", "seeding_time"),
  )
