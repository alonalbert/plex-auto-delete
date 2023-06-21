package com.alonalbert.pad.app.ui.padscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun <T> PadDialogScreen(
    viewModel: PadDialogViewModel<T>,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    dialog: @Composable (T, () -> Unit) -> Unit,
    content: @Composable () -> Unit,
) {
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    PadScreen(
        viewModel = viewModel,
        floatingActionButton = floatingActionButton,
        modifier = modifier,
    ) {
        val onDismiss = { viewModel.dismissDialog() }

        dialogState?.let {
            dialog(it, onDismiss)
        }
        content()
    }
}