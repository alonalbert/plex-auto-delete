package com.alonalbert.pad.app.ui.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.UserWithShows
import com.alonalbert.pad.app.ui.DestinationsArgs.USER_ID_ARG
import com.alonalbert.pad.app.ui.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : PadViewModel() {
    private val messageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val isLoadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val userId: Long = savedStateHandle[USER_ID_ARG]!!

    val userState: StateFlow<UserWithShows?> = repository.getUserFlow(userId).stateIn(viewModelScope, null)

    val messageState: StateFlow<Int?> = messageFlow.stateIn(viewModelScope, null)
    val isLoadingState: StateFlow<Boolean> = isLoadingFlow.stateIn(viewModelScope, false)

    init {
        refresh()
    }

    override fun setMessage(id: Int?) {
        messageFlow.value = id
    }

    override fun setIsLoading(isLoading: Boolean) {
        isLoadingFlow.value = isLoading
    }

    override suspend fun refreshData() {
        repository.refreshShows()
    }

    fun updateUser(user: User) {
        Timber.d("updateUser ${user.name}")
        repository.updateRepository(R.string.user_updated) {
            updateUser(user)
        }
    }

    fun deleteShow(user: User, show: Show) {
        Timber.d("Delete show ${show.name} from user ${user.name}")
        repository.updateRepository(R.string.show_deleted) {
            // TODO:
        }
    }

    private fun Repository.updateRepository(
        okMessage: Int?,
        errorMessage: Int? = R.string.network_error,
        block: suspend Repository.() -> Unit,
    ) {
        viewModelScope.launch {
            isLoadingFlow.value = true
            runCatching {
                block()
                setMessage(okMessage)
            }.onFailure {
                Timber.e(it, "Failed to update repository")
                setMessage(errorMessage)
            }
            isLoadingFlow.value = false
        }
    }
}
