package com.alonalbert.pad.server.deluge.model.request

class AuthLogin(password: String = "") : Request<AuthLogin, Boolean>("auth.login", password)