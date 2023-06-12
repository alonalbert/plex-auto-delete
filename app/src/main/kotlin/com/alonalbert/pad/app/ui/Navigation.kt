package com.alonalbert.pad.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.DestinationsArgs.USER_ID_ARG
import com.alonalbert.pad.app.ui.userdetail.UserDetailScreen
import com.alonalbert.pad.app.ui.userlist.UserListScreen

object DestinationsArgs {
    const val USER_ID_ARG = "userId"
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "users") {
        composable("users") {
            UserListScreen(
                onUserClick = { navController.navigateToUserDetail(it) },
                modifier = Modifier.padding(8.dp)
            )
        }
        composable(
            "user/{$USER_ID_ARG}",
            arguments = listOf(navArgument(USER_ID_ARG) { type = NavType.LongType }),
        ) {
            UserDetailScreen(modifier = Modifier.padding(8.dp))
        }
    }
}

fun NavHostController.navigateToUserDetail(user: User) {
    navigate("user/${user.id}")
}
