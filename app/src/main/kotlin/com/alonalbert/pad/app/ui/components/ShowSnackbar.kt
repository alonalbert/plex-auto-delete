package com.alonalbert.pad.app.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.alonalbert.pad.app.ui.padscreen.PadViewModel

@Composable
fun ShowSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    viewModel: PadViewModel,
) {
    LaunchedEffect(snackbarHostState, viewModel, message) {
        snackbarHostState.showSnackbar(message)
        viewModel.setMessage(null)
    }
}
