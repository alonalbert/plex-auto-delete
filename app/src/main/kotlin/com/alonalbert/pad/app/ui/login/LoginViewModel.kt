package com.alonalbert.pad.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val serverState: StateFlow<String?> = settings.observeString("server").stateIn(viewModelScope, "")
    private val usernameState: StateFlow<String?> = settings.observeString("username").stateIn(viewModelScope, "")
    private val passwordState: StateFlow<String?> = settings.observeString("password").stateIn(viewModelScope, "")

    val loginState = combine(serverState, usernameState, passwordState) { server, username, password ->
        when {
            server.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() -> null
            else -> LoginState(server, username, password)
        }
    }

    fun setLoginState(server: String, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setValue("server", server)
            settings.setValue("username", username)
            settings.setValue("password", password)
        }
    }

    data class LoginState(val server: String, val username: String, val password: String)
}
