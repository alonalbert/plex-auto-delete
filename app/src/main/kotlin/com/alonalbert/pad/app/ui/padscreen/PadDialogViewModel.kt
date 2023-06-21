package com.alonalbert.pad.app.ui.padscreen

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.util.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class PadDialogViewModel<T>(application: Application) : PadViewModel(application) {
    private val dialogFlow: MutableStateFlow<T?> = MutableStateFlow(null)

    val dialogState: StateFlow<T?> = dialogFlow.stateIn(viewModelScope, null)

    private fun setDialogState(dialogState: T) {
        dialogFlow.value = dialogState
    }

    fun dismissDialog() {
        dialogFlow.value = null
    }

    protected fun runTaskWithDialogResult(
        errorMessage: String = application.getString(R.string.network_error),
        task: suspend () -> T,
    ) {
        setLoading()
        viewModelScope.launch {
            try {
                setDialogState(task())
            } catch (e: Throwable) {
                setMessage(errorMessage)
            } finally {
                dismissLoading()
            }
        }
    }

}
