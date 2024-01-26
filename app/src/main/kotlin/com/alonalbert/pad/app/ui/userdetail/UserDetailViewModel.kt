package com.alonalbert.pad.app.ui.userdetail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.navigation.DestinationsArgs.USER_ID_ARG
import com.alonalbert.pad.app.ui.padscreen.PadViewModel
import com.alonalbert.pad.app.util.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
  private val repository: Repository,
  application: Application,
  savedStateHandle: SavedStateHandle,
) : PadViewModel(application) {
  private val userId: Long = savedStateHandle[USER_ID_ARG]!!

  val userState: StateFlow<User?> = repository.getUserFlow(userId).stateIn(viewModelScope, null)

  init {
    refresh()
  }

  override suspend fun refreshData() {
    repository.refreshShows()
  }

  fun updateUser(user: User) {
    Timber.d("updateUser ${user.name}")
    repository.doUpdate(application.getString(R.string.user_updated)) {
      updateUser(user)
    }
  }
}
