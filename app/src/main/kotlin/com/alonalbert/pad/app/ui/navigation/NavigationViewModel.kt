package com.alonalbert.pad.app.ui.navigation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.settings.LOGGED_IN
import com.alonalbert.pad.app.data.settings.dataStore
import com.alonalbert.pad.app.data.settings.updateSettings
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.Loading
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.LoggedIn
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.LoggedOut
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    val loginState = application.dataStore.data.map {
        when (it[LOGGED_IN]) {
            true -> LoggedIn
            false -> LoggedOut
            null -> Loading
        }
    }.stateIn(viewModelScope, Loading)

    fun setLoggedIn(value: Boolean) {
        with(viewModelScope) {
            application.updateSettings {
                set(LOGGED_IN, value)
            }
        }
    }

    sealed class LoginState {
        object LoggedIn : LoginState()
        object LoggedOut : LoginState()
        object Loading : LoginState()
    }
}
