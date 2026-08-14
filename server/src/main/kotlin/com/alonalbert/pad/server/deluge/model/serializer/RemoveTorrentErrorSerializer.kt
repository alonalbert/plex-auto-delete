package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.RemoveTorrentError

object RemoveTorrentErrorSerializer : ListToPojoSerializer<RemoveTorrentError>(RemoveTorrentError::class)
