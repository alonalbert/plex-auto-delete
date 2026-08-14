package com.alonalbert.pad.server.deluge.model.serializer

import com.alonalbert.pad.server.deluge.model.response.Host

object HostSerializer : ListToPojoSerializer<Host>(Host::class)
