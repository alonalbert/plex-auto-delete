package com.alonalbert.pad.app.ui.userlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.padscreen.PadDialogViewModel
import com.alonalbert.pad.app.ui.userlist.UserListViewModel.DialogState
import com.alonalbert.pad.app.ui.userlist.UserListViewModel.DialogState.AutoDeleteDialog
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
) : PadDialogViewModel<DialogState>(application) {

    val userListState: StateFlow<List<User>> = repository.getUsersFlow().stateIn(viewModelScope, emptyList())

    init {
        refresh()
    }

    override suspend fun refreshData() {
        repository.refreshUsers()
    }

    fun runAutoWatch() {
        runTaskWithDialogResult {
            DialogState.AutoWatchDialog(repository.runAutoWatch())
        }
    }

    fun runAutoDelete(days: Int, isTestMode: Boolean) {
        runTaskWithDialogResult {
            AutoDeleteDialog(repository.runAutoDelete(days, isTestMode))
        }
    }

    sealed class DialogState {
        class AutoDeleteDialog(val result: AutoDeleteResult) : DialogState()
        class AutoWatchDialog(val result: AutoWatchResult) : DialogState()
    }
}

