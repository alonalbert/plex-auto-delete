package com.alonalbert.pad.app.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.util.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class PadViewModel(private val application: Application) : ViewModel() {
    private val messageFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    protected val isLoadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val messageState: StateFlow<String?> = messageFlow.stateIn(viewModelScope, null)
    val isLoadingState: StateFlow<Boolean> = isLoadingFlow.stateIn(viewModelScope, false)

    fun refresh() {
        viewModelScope.launch {
            setIsLoading(true)
            try {
                refreshData()
            } catch (e: Throwable) {
                Timber.e(e, "Failed to refresh users")
                setMessage(application.getString(R.string.network_error))
            } finally {
                setIsLoading(false)
            }
        }
    }

    protected abstract suspend fun refreshData()

    fun setMessage(message: String?) {
        messageFlow.value = message
    }

    fun setIsLoading(isLoading: Boolean) {
        isLoadingFlow.value = isLoading
    }
}
