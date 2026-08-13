package com.alonalbert.pad.server.deluge.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Error(val code: Int, val message: String)
