package com.alonalbert.pad.server.deluge.model.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "deluge")
data class DelugeProperties(val labels: List<LabelConfig> = emptyList())