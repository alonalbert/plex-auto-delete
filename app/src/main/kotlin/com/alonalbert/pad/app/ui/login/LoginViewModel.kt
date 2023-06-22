package com.alonalbert.pad.app.ui.login

import android.app.Application
import androidx.datastore.preferences.core.MutablePreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.settings.LOGGED_IN
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

    val loginInfo = application.dataStore.data.map {
        val server = it[SERVER]
        val username = it[USERNAME]
        val password = it[PASSWORD]
        when {
            server.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank() -> null
            else -> LoginInfo(server, username, password)
        }
    }.stateIn(viewModelScope, null)

    val isLoggedIn = application.dataStore.data.map { it[LOGGED_IN] ?: false }.stateIn(viewModelScope, false)

    fun saveLoginInfo(server: String, username: String, password: String) {
        updateSettings {
            set(SERVER, server)
            set(USERNAME, username)
            set(PASSWORD, password)
        }
    }

    fun setLoggedIn() {
        updateSettings {
            set(LOGGED_IN, true)
        }
    }

    private fun updateSettings(block: suspend MutablePreferences.() -> Unit) {
        viewModelScope.launch {
            application.dataStore.updateData {
                it.toMutablePreferences().apply {
                    block()
                }
            }
        }
    }

    data class LoginInfo(val server: String = "", val username: String = "", val password: String = "")
}
