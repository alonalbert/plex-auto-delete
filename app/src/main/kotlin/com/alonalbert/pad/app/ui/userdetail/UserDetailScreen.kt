package com.alonalbert.pad.app.ui.userdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.util.LoadingContent
import com.alonalbert.pad.app.util.ShowSnackbar

@Composable
fun UserDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val user by viewModel.userState.collectAsStateWithLifecycle()
    val message by viewModel.messageState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { /* todo */ }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_show))
            }
        }
    ) { padding ->

        UserDetailContent(
            isLoading = isLoading,
            user = user,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        )

        message?.let {
            ShowSnackbar(snackbarHostState, it, viewModel)
        }
    }
}

@Composable
fun UserDetailContent(
    isLoading: Boolean,
    user: User?,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
) {
    LoadingContent(
        isLoading = isLoading || user == null,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        if (user != null) {
            Text(text = user.name)
        }
    }
}

private fun User.UserType.toggle() = when (this) {
    User.UserType.EXCLUDE -> User.UserType.INCLUDE
    User.UserType.INCLUDE -> User.UserType.EXCLUDE
}

@Preview
@Composable
fun UserDetailContentPreview() {
    UserDetailContent(isLoading = false, user = User(name = "Bob"))
}