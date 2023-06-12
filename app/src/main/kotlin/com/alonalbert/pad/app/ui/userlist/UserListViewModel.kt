package com.alonalbert.pad.app.ui.userlist

import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(private val repository: Repository) : PadViewModel() {
    private val messageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val isLoadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val messageState: StateFlow<Int?> = messageFlow.stateIn(viewModelScope, null)
    val isLoadingState: StateFlow<Boolean> = isLoadingFlow.stateIn(viewModelScope, false)
    val userListState: StateFlow<List<User>> = repository.getUsersFlow().stateIn(viewModelScope, emptyList())

    init {
        refresh()
    }

    override suspend fun refreshData() {
        repository.refreshUsers()
    }

    override fun clearMessage() {
        messageFlow.value = null
    }

    override fun setIsLoading(isLoading: Boolean) {
        isLoadingFlow.value = isLoading
    }

    override fun setMessage(id: Int) {
        messageFlow.value = id
    }
}

