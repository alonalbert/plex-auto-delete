package com.alonalbert.pad.app.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.settings.PASSWORD
import com.alonalbert.pad.app.data.settings.SERVER
import com.alonalbert.pad.app.data.settings.USERNAME
import com.alonalbert.pad.app.data.settings.dataStore
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    val loginState = application.dataStore.data.map {
        val server = it[SERVER]
        val username = it[USERNAME]
        val password = it[PASSWORD]
        when {
            server.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() -> null
            else -> LoginState(server, username, password)
        }
    }.stateIn(viewModelScope, null)

    fun setLoginState(server: String, username: String, password: String) {
        viewModelScope.launch {
            application.dataStore.updateData {
                val mutablePreferences = it.toMutablePreferences()
                mutablePreferences.set(SERVER, server)
                mutablePreferences.set(USERNAME, username)
                mutablePreferences.set(PASSWORD, password)
                mutablePreferences
            }
        }
    }

    data class LoginState(val server: String = "", val username: String = "", val password: String = "")
}
