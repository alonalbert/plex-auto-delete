package com.alonalbert.pad.app.service

import com.alonalbert.pad.app.database.User

sealed class UpdateUserResult {
    data class Success(val user: User) : UpdateUserResult()
    data class Error(val throwable: Throwable) : UpdateUserResult()
}
