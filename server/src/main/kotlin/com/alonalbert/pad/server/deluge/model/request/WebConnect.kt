package com.alonalbert.pad.server.deluge.model.request

class WebConnect(hostId: String) : Request<WebConnect, List<String>>("web.connect", hostId)