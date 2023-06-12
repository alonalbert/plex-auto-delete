package com.alonalbert.pad.app.ui.userlist

import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(private val repository: Repository) : PadViewModel() {
    private val messageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val isLoadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val userFlowsById = mutableMapOf<Long, StateFlow<User>>()

    val messageState: StateFlow<Int?> = messageFlow.stateIn(viewModelScope, null)
    val isLoadingState: StateFlow<Boolean> = isLoadingFlow.stateIn(viewModelScope, false)
    val userListState: StateFlow<List<StateFlow<User>>> = repository.getUsersFlow().map { users ->
        val newUsers = users.filter { !userFlowsById.containsKey(it.id) }
        newUsers.forEach {
            userFlowsById[it.id] = repository.getUserFlow(it.id).stateIn(viewModelScope, it)
        }
        userFlowsById.values.toList()
    }.catch {
        Timber.e(it, "Failed to load users")
        setMessage(R.string.network_error)
    }.stateIn(viewModelScope, emptyList())

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

