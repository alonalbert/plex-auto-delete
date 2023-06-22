package com.alonalbert.pad.app.ui.userlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.AutoDeleteResult
import com.alonalbert.pad.app.data.AutoWatchResult
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.data.User.UserType.EXCLUDE
import com.alonalbert.pad.app.data.User.UserType.INCLUDE
import com.alonalbert.pad.app.ui.padscreen.PadDialogScreen
import com.alonalbert.pad.app.ui.theme.MyApplicationTheme
import com.alonalbert.pad.app.ui.userlist.UserListViewModel.DialogState.AutoDeleteDialog
import com.alonalbert.pad.app.ui.userlist.UserListViewModel.DialogState.AutoWatchDialog
import com.alonalbert.pad.app.util.toByteUnitString
import timber.log.Timber

@Composable
fun UserListScreen(
    onLogout: () -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val users by viewModel.userListState.collectAsStateWithLifecycle()

    val onAutoWatchClick = { viewModel.runAutoWatch() }
    val onAutoDeleteClick = { viewModel.runAutoDelete() }

    PadDialogScreen(
        viewModel = viewModel,
        onLogout = onLogout,
        dialog = { dialogState, onDismiss ->
            when (dialogState) {
                is AutoWatchDialog -> AutoWatchResultDialog(result = dialogState.result, onDismiss = onDismiss)
                is AutoDeleteDialog -> AutoDeleteResultDialog(result = dialogState.result, onDismiss = onDismiss)
            }
        },
        modifier = modifier,
    ) {
        UserListScreen(
            users = users,
            onAutoWatchClick = onAutoWatchClick,
            onAutoDeleteClick = onAutoDeleteClick,
            onUserClick = onUserClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AutoWatchResultDialog(
    result: AutoWatchResult,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.auto_watch_results)) },
        text = {
            val text = when {
                result.users.isEmpty() -> stringResource(R.string.auto_watch_noop)
                else -> result.users.entries.joinToString("\n") { (user, shows) -> "${user}: ${shows.joinToString { show -> show }}" }
            }
            Text(text = text)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
fun AutoDeleteResultDialog(
    result: AutoDeleteResult,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.auto_watch_results)) },
        text = {
            val text1 = stringResource(R.string.auto_delete_text1, result.numBytes.toByteUnitString(), result.numFiles)
            val text2 = stringResource(R.string.auto_delete_text2, result.shows.joinToString { it })
            Column {
                Text(text = text1)
                Text(text = text2)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
internal fun UserListScreen(
    users: List<User>,
    onAutoWatchClick: () -> Unit,
    onAutoDeleteClick: () -> Unit,
    onUserClick: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        ActionButtons(onAutoWatchClick, onAutoDeleteClick)

        UsersTitle()

        UsersList(users, onUserClick)
    }
}

@Composable
private fun ActionButtons(
    onAutoWatchClick: () -> Unit,
    onAutoDeleteClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onAutoWatchClick) {
            Text(text = "Auto Watch")
        }
        Button(onClick = onAutoDeleteClick) {
            Text(text = "Auto Delete")
        }
    }
}

@Composable
private fun UsersList(
    users: List<User>,
    onUserClick: (user: User) -> Unit
) {
    LazyColumn {
        items(items = users) {
            UserCard(it, onUserClick)
        }
    }
}

@Composable
private fun UsersTitle() {
    Text(
        text = stringResource(R.string.users),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun UserCard(user: User, onUserClick: (User) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
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
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = user.displayType(),
            )
        }
    }
}

@Composable
private fun User.displayType() = when (type) {
    EXCLUDE -> pluralStringResource(id = R.plurals.excludes_shows, count = shows.size, shows.size)
    INCLUDE -> pluralStringResource(id = R.plurals.includes_shows, count = shows.size, shows.size)
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        val users = listOf(User(name = "Mark"), User(name = "Ted"), User(name = "Bob"))
        UserListScreen(
            users,
            onAutoWatchClick = {},
            onAutoDeleteClick = {},
            onUserClick = {},
        )
    }
}
