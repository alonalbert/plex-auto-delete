package com.alonalbert.pad.app.ui.userdetail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.components.baselineHeight
import com.alonalbert.pad.app.util.LoadingContent
import com.alonalbert.pad.app.util.ShowSnackbar

@Composable
fun UserDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val toggleUser = { user: User -> viewModel.updateUser(user.copy(type = user.type.toggle())) }

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

        LoadingContent(
            isLoading = isLoading || user == null,
            onRefresh = viewModel::refresh,
            modifier = modifier.padding(padding)
        ) {
            user?.let { UserDetailContent(user = it, onUserTypeClick = toggleUser) }
        }

        message?.let {
            ShowSnackbar(snackbarHostState, it, viewModel)
        }
    }
}

@Composable
fun UserDetailContent(
    user: User,
    onUserTypeClick: (user: User) -> Unit,
    nestedScrollInteropConnection: NestedScrollConnection = rememberNestedScrollInteropConnection()
) {
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollInteropConnection)
            .systemBarsPadding()
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                ProfileHeader(
                    scrollState,
                    this@BoxWithConstraints.maxHeight
                )
                UserInfoFields(
                    user = user,
                    onUserTypeClick = onUserTypeClick,
                    containerHeight = this@BoxWithConstraints.maxHeight
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    containerHeight: Dp
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) { offset.toDp() }

    Icon(
        modifier = Modifier
            .heightIn(max = containerHeight / 3)
            .fillMaxSize()
            // TODO: Update to use offset to avoid recomposition
            .padding(
                start = 16.dp,
                top = offsetDp,
                end = 16.dp
            )
            .clip(CircleShape),
        painter = painterResource(id = R.drawable.baseline_person_24),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null
    )
}

@Composable
private fun UserInfoFields(
    user: User,
    onUserTypeClick: (user: User) -> Unit,
    containerHeight: Dp,
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Name(user)

        UserType(user, onUserTypeClick)
//        ProfileProperty(stringResource(R.string.display_name), userData.displayName)
//
//        ProfileProperty(stringResource(R.string.status), userData.status)
//
//        ProfileProperty(stringResource(R.string.twitter), userData.twitter, isLink = true)
//
//        userData.timeZone?.let {
//            ProfileProperty(stringResource(R.string.timezone), userData.timeZone)
//        }

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.height((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun Name(user: User, modifier: Modifier = Modifier) {
    Text(
        text = user.name,
        modifier = modifier
            .padding(16.dp)
            .baselineHeight(32.dp),
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun UserType(user: User, onUserTypeClick: (User) -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Text(
                text = user.type.displayText(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            FilledIconButton(
                onClick = { onUserTypeClick(user) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.clickable { }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.swap_horiz_48),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}


private fun User.UserType.toggle() = when (this) {
    User.UserType.EXCLUDE -> User.UserType.INCLUDE
    User.UserType.INCLUDE -> User.UserType.EXCLUDE
}

@Composable
private fun User.UserType.displayText() = when (this) {
    User.UserType.EXCLUDE -> stringResource(R.string.user_type_excluding)
    User.UserType.INCLUDE -> stringResource(R.string.user_type_including)
}

@Preview
@Composable
fun UserDetailContentPreview() {
    UserDetailContent(user = User(name = "Bob", type = User.UserType.EXCLUDE), { })
}