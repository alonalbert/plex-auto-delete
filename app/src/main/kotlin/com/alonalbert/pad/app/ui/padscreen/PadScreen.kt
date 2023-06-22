package com.alonalbert.pad.app.ui.padscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R

@Composable
fun PadScreen(
    viewModel: PadViewModel,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    onLogout: () -> Unit,
    content: @Composable () -> Unit,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.messageState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingState.collectAsStateWithLifecycle()

    val onLogoutClick = { onLogout() }

    Scaffold(
        topBar = { PadTopBar(onLogoutClick) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = floatingActionButton,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LoadingContent(
            isLoading = isLoading,
            onRefresh = viewModel::refresh,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content()
        }

        message?.let {
            ShowSnackbar(snackbarHostState, it, viewModel)
        }
    }
}

@Composable
private fun PadTopBar(onLogoutClick: () -> Unit) {
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(id = R.string.logout),
                )
            }
        }
    )

}

@Composable
private fun ShowSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    viewModel: PadViewModel,
) {
    LaunchedEffect(snackbarHostState, viewModel, message) {
        snackbarHostState.showSnackbar(message)
        viewModel.dismissMessage()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isLoading, onRefresh = onRefresh)
    Box(modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
