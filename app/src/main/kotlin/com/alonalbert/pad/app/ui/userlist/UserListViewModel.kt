package com.alonalbert.pad.app.ui.userlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
) : PadViewModel(application) {
    val userListState: StateFlow<List<User>> = repository.getUsersFlow().stateIn(viewModelScope, emptyList())

    init {
        refresh()
    }

    override suspend fun refreshData() {
        repository.refreshUsers()
    }
}

