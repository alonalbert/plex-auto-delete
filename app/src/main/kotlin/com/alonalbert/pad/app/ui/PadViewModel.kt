package com.alonalbert.pad.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.database.PadDatabase
import com.alonalbert.pad.app.database.User
import com.alonalbert.pad.app.service.PadService
import com.alonalbert.pad.app.service.UpdateUserResult
import com.alonalbert.pad.app.service.UsersResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PadViewModel @Inject constructor(
    private val database: PadDatabase,
    private val service: PadService,
) : ViewModel() {
    private val scope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO)

    fun getUsers(): LiveData<List<User>> {
        val userDao = database.userDao()
        scope.launch(Dispatchers.IO) {
            when (val result = service.loadUsers()) {
                is UsersResult.Success -> database.userDao().insertAll(result.users)
                is UsersResult.Error -> Timber.e(result.throwable, "Failed to load users")
            }
        }

        return userDao.getAll()
    }

    fun updateUser(user: User) {
        scope.launch {
            when (val result = service.updateUser(user)) {
                is UpdateUserResult.Success -> database.userDao().update(result.user)
                is UpdateUserResult.Error -> Timber.e(result.throwable, "Failed to update user ${user.id}")
            }
        }
    }
}
