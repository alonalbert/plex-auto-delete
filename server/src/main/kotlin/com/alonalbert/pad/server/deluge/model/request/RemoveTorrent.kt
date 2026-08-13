package com.alonalbert.pad.server.deluge.model.request

class RemoveTorrent(
  id: String,
  removeData: Boolean = false,
) : Request<RemoveTorrent, Boolean>("core.remove_torrent", id, removeData)
