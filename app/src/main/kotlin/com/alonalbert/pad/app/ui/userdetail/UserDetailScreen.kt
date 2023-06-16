package com.alonalbert.pad.app.ui.userdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.components.PadScaffold
import com.alonalbert.pad.app.ui.components.baselineHeight
import timber.log.Timber

@Composable
fun UserDetailScreen(
    onEditShowsClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {

    val userState by viewModel.userState.collectAsStateWithLifecycle()

    // TODO: Implement empty content
    userState?.let { user ->
        val toggleUser = { viewModel.toggleUser(userState) }
        val deleteShow = { show: Show -> viewModel.deleteShow(user, show) }

        PadScaffold(
            viewModel = viewModel,
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(onClick = { onEditShowsClick(user) }) {
                    Icon(Icons.Filled.Edit, stringResource(id = R.string.add_show))
                }
            }
        ) {
            UserDetailContent(
                user = user,
                shows = user.shows,
                onUserTypeClick = toggleUser,
                onDeleteShowClick = deleteShow,
            )
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    shows: List<Show>,
    onUserTypeClick: () -> Unit,
    onDeleteShowClick: (Show) -> Unit,
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ProfileHeader(
                    this@BoxWithConstraints.maxHeight
                )
                UserInfoFields(
                    user = user,
                    shows = shows,
                    onUserTypeClick = onUserTypeClick,
                    onDeleteShowClick = onDeleteShowClick,
                    containerHeight = this@BoxWithConstraints.maxHeight
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    containerHeight: Dp
) {
    Icon(
        modifier = Modifier
            .heightIn(max = containerHeight / 3)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clip(CircleShape),
        painter = painterResource(id = R.drawable.baseline_person_24),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null
    )
}

@Composable
private fun UserInfoFields(
    user: User,
    shows: List<Show>,
    onUserTypeClick: () -> Unit,
    containerHeight: Dp,
    onDeleteShowClick: (Show) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Name(user)

        UserType(user, onUserTypeClick)

        ShowsList(shows, onDeleteShowClick = onDeleteShowClick)

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
fun UserType(user: User, onUserTypeClick: () -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = user.type.displayText(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            FilledIconButton(
                onClick = onUserTypeClick,
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

@Composable
fun ShowsList(shows: List<Show>, onDeleteShowClick: (Show) -> Unit) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()

        Text(
            text = stringResource(R.string.shows),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(vertical = 8.dp)
        )

        LazyColumn {
            shows.forEach {
                item {
                    ShowCard(
                        show = it,
                        onDeleteShowClick = onDeleteShowClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowCard(show: Show, onDeleteShowClick: (Show) -> Unit) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, Color.Blue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Timber.d("Composing card for $show")
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                text = show.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onDeleteShowClick(show) }) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}


@Composable
private fun User.UserType.displayText() = when (this) {
    User.UserType.EXCLUDE -> stringResource(R.string.user_type_excluding)
    User.UserType.INCLUDE -> stringResource(R.string.user_type_including)
}

private fun User.UserType.toggle() = when (this) {
    User.UserType.EXCLUDE -> User.UserType.INCLUDE
    User.UserType.INCLUDE -> User.UserType.EXCLUDE
}

private fun UserDetailViewModel.toggleUser(user: User?) {
    if (user != null) {
        updateUser(user.copy(type = user.type.toggle()))
    }
}

@Preview
@Composable
fun UserDetailContentPreview() {
    UserDetailContent(
        user = User(name = "Bob", type = User.UserType.EXCLUDE),
        shows = listOf(
            Show(name = "Dexter"),
            Show(name = "Breaking Bad")
        ),
        onUserTypeClick = { },
        onDeleteShowClick = { }
    )
}