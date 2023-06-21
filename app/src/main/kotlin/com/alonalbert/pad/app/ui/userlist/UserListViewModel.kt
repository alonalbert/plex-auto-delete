package com.alonalbert.pad.app.ui.userlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.padscreen.PadViewModel
import com.alonalbert.pad.app.ui.userlist.UserListViewModel.DialogState.AutoDeleteDialog
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
    private val dialogFlow: MutableStateFlow<DialogState?> = MutableStateFlow(null)

    val userListState: StateFlow<List<User>> = repository.getUsersFlow().stateIn(viewModelScope, emptyList())
    val dialogState: StateFlow<DialogState?> = dialogFlow.stateIn(viewModelScope, null)

    init {
        refresh()
    }

    override suspend fun refreshData() {
        repository.refreshUsers()
    }

    fun runAutoWatch() {
        runTaskWithDialogResult {
            DialogState.AutoWatchDialog(AutoWatchResult(listOf(User(name = "Foo"))))
//            DialogState.AutoWatchDialog(repository.runAutoWatch())
        }
    }

    fun runAutoDelete() {
        runTaskWithDialogResult {
            AutoDeleteDialog(AutoDeleteResult(10, 10000000, setOf("Show")))
//            AutoDeleteDialog(repository.runAutoDelete())
        }
    }

    private fun runTaskWithDialogResult(task: suspend () -> DialogState) {
        setLoading()
        viewModelScope.launch {
            try {
                setDialogState(task())
            } catch (e: Throwable) {
                setMessage(application.getString(R.string.network_error))
            } finally {
                dismissLoading()
            }
        }
    }

    private fun setDialogState(value: DialogState) {
        dialogFlow.value = value
    }

    fun dismissDialog() {
        dialogFlow.value = null
    }

    sealed class DialogState {
        class AutoDeleteDialog(val result: AutoDeleteResult) : DialogState()
        class AutoWatchDialog(val result: AutoWatchResult) : DialogState()
    }
}

