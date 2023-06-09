package com.alonalbert.pad.app.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

@Composable
fun PadApp() {
    val viewModel = hiltViewModel<PadViewModel>()
    val users by viewModel.getUsers().observeAsState(emptyList())
    LazyColumn {
        Timber.i("Updating ${users.size} users")
        users.forEach {
            item {
                Text(text = it.name)
            }
        }
    }
}
