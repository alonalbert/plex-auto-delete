package com.alonalbert.pad.app.service

import com.alonalbert.pad.app.database.User

sealed class UsersResult {
    data class Success(val users: List<User>) : UsersResult()
    data class Error(val throwable: Throwable) : UsersResult()
}
