package com.alonalbert.pad.app.ui.editshows

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.DestinationsArgs
import com.alonalbert.pad.app.ui.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EditShowsViewModel @Inject constructor(
    private val repository: Repository,
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : PadViewModel(application) {

    private val userId: Long = savedStateHandle[DestinationsArgs.USER_ID_ARG]!!
    private val filterFlow: MutableStateFlow<String> = MutableStateFlow("")

    val showListState: StateFlow<List<Show>> = repository.getShowsFlow().stateIn(viewModelScope, emptyList())
    val userState: StateFlow<User?> = repository.getUserFlow(userId).stateIn(viewModelScope, null)
    val filterState: StateFlow<String> = filterFlow.stateIn(viewModelScope, "")

    fun setFilter(filter: String) {
        filterFlow.value = filter
    }

    override suspend fun refreshData() {
        repository.refreshShows()
    }

    fun updateUser(user: User) {
        return repository.doUpdate(okMessage = application.getString(R.string.saved_shows)) {
            updateUser(user)
        }
    }
}