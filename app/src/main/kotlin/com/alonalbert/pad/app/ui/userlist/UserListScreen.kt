package com.alonalbert.pad.app.ui.userlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.User.UserType.EXCLUDE
import com.alonalbert.pad.app.data.User.UserType.INCLUDE
import com.alonalbert.pad.app.ui.theme.MyApplicationTheme
import com.alonalbert.pad.app.util.LoadingContent
import com.alonalbert.pad.app.util.ShowSnackbar
import timber.log.Timber

@Composable
fun UserListScreen(
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val message by viewModel.messageState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingState.collectAsStateWithLifecycle()
    val users by viewModel.userListState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        LoadingContent(
            isLoading = isLoading,
            onRefresh = viewModel::refresh,
            modifier = modifier.padding(padding)
        ) {
            UserListScreen(
                items = users,
                onUserClick = onUserClick,
            )
        }

        message?.let {
            ShowSnackbar(snackbarHostState, it, viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserListScreen(
    items: List<User>,
    onUserClick: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        stickyHeader {
            Text(
                text = "Users",
                fontSize = 24.sp,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(bottom = 10.dp)
            )
        }
        items.forEach {
            item {
                UserCard(it, onUserClick)
            }
        }
    }
}

@Composable
private fun UserCard(user: User, onUserClick: (User) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, Color.Blue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onUserClick(user) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Timber.d("Composing card for $user")
            Text(
                text = user.name,
                fontSize = 30.sp,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = user.displayType(),
            )
        }
    }
}

private fun User.displayType() = when (type) {
    EXCLUDE -> "Excludes 0 shows"
    INCLUDE -> "Includes 0 shows"
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        val users = listOf(User(name = "Mark"), User(name = "Ted"), User(name = "Bob"))
        UserListScreen(
            users,
            onUserClick = {})
    }
}
