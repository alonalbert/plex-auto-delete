package com.alonalbert.pad.server.deluge.model.request

class RemoveTorrents(
  ids: Collection<String>,
  removeData: Boolean = false,
) : Request<RemoveTorrents, Boolean>("core.remove_torrents", ids, removeData)
