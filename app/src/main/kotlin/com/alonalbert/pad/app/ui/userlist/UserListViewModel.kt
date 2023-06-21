package com.alonalbert.pad.app.ui.userlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.padscreen.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: Repository,
    private val application: Application,
) : PadViewModel(application) {
    private val autoWatchResultFlow: MutableStateFlow<List<User>?> = MutableStateFlow(null)
    private val autoDeleteResultFlow: MutableStateFlow<AutoDeleteResult?> = MutableStateFlow(null)

    val userListState: StateFlow<List<User>> = repository.getUsersFlow().stateIn(viewModelScope, emptyList())
    val autoWatchResultState: StateFlow<List<User>?> = autoWatchResultFlow.stateIn(viewModelScope, null)
    val autoDeleteResultState: StateFlow<AutoDeleteResult?> = autoDeleteResultFlow.stateIn(viewModelScope, null)

    init {
        refresh()
    }

    override suspend fun refreshData() {
        repository.refreshUsers()
    }

    fun runAutoWatch() {
        setIsLoading(true)
        viewModelScope.launch {
            try {
                setAutoWatchResult(repository.runAutoWatch())
            } catch (e: Throwable) {
                setMessage(application.getString(R.string.network_error))
            } finally {
                setIsLoading(false)
            }
        }
    }

    fun runAutoDelete() {
        setIsLoading(true)
        viewModelScope.launch {
            try {
                setAutoDeleteResult(repository.runAutoDelete())
            } catch (e: Throwable) {
                setMessage(application.getString(R.string.network_error))
            } finally {
                setIsLoading(false)
            }
        }
    }

    fun setAutoWatchResult(value: List<User>?) {
        autoWatchResultFlow.value = value
        if (value != null) {
            autoDeleteResultFlow.value = null
        }
    }

    fun setAutoDeleteResult(value: AutoDeleteResult?) {
        autoDeleteResultFlow.value = value
        if (value != null) {
            autoWatchResultFlow.value = null
        }
    }
}

