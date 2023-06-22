package com.alonalbert.pad.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.settings.Settings.Companion.PASSWORD
import com.alonalbert.pad.app.data.settings.Settings.Companion.SERVER
import com.alonalbert.pad.app.data.settings.Settings.Companion.USERNAME
import com.alonalbert.pad.app.data.settings.SettingsDao
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val settings: SettingsDao,
) : ViewModel() {
    private val serverState: StateFlow<String?> = settings.observeString(SERVER).stateIn(viewModelScope, "")
    private val usernameState: StateFlow<String?> = settings.observeString(USERNAME).stateIn(viewModelScope, "")
    private val passwordState: StateFlow<String?> = settings.observeString(PASSWORD).stateIn(viewModelScope, "")

    val loginState = combine(serverState, usernameState, passwordState) { server, username, password ->
        when {
            server.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() -> null
            else -> LoginState(server, username, password)
        }
    }

    fun setLoginState(server: String, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setValue(SERVER, server)
            settings.setValue(USERNAME, username)
            settings.setValue(PASSWORD, password)
        }
    }

    data class LoginState(val server: String, val username: String, val password: String)
}
