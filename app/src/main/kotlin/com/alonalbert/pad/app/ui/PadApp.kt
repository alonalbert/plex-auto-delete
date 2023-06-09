package com.alonalbert.pad.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alonalbert.pad.app.database.User
import com.alonalbert.pad.app.database.User.UserType.EXCLUDE
import com.alonalbert.pad.app.database.User.UserType.INCLUDE
import com.alonalbert.pad.app.ui.theme.AppTheme
import timber.log.Timber

@Composable
fun PadApp() {
    val viewModel = hiltViewModel<PadViewModel>()
    val users by viewModel.getUsers().observeAsState(emptyList())
    UserList(users)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserList(users: List<User>) {
    LazyColumn {
        stickyHeader {
            Text(
                text = "Users",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(bottom = 10.dp)
            )
        }
        Timber.i("Updating ${users.size} users")
        users.forEach {
            item {
                UserCard(user = it)
            }
        }
    }
}

@Composable
private fun UserCard(user: User) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, Color.Blue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
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

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    AppTheme {
        UserList(
            users = listOf(
                User(name = "Alon"),
                User(name = "Matan"),
                User(name = "Dean"),
            )
        )
    }
}
