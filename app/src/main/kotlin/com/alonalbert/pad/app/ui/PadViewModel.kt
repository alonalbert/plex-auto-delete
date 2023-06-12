package com.alonalbert.pad.app.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.R
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class PadViewModel(private val application: Application) : ViewModel() {
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

    abstract fun setMessage(message: String?)

    abstract fun setIsLoading(isLoading: Boolean)
}
