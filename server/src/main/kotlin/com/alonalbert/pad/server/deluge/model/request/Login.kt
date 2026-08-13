package com.alonalbert.pad.server.deluge.model.request

class Login(password: String): Request<Login, Boolean>("auth.login", password)