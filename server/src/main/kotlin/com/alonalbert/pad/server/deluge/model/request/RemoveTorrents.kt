package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError

class RemoveTorrents(
  ids: Collection<String>,
  removeData: Boolean = false,
) : Request<RemoveTorrents, List<RemoveTorrentError>>("core.remove_torrents", ids, removeData)
