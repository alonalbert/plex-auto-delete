package com.alonalbert.pad.server.deluge.model.request

import com.alonalbert.pad.server.deluge.model.response.Host

class WebGetHosts : Request<WebGetHosts, List<Host>>("web.get_hosts")