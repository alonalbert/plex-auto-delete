package com.alonalbert.pad.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.DestinationsArgs.USER_ID_ARG
import com.alonalbert.pad.app.ui.editshows.EditShowsScreen
import com.alonalbert.pad.app.ui.login.LoginScreen
import com.alonalbert.pad.app.ui.userdetail.UserDetailScreen
import com.alonalbert.pad.app.ui.userlist.UserListScreen

object DestinationsArgs {
    const val USER_ID_ARG = "userId"
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoggedIn = { navController.navigateToMainScreen() })
        }
        composable("users") {
            UserListScreen(
                onUserClick = { navController.navigateToUserDetail(it) },
            )
        }
        composable(
            "user/{$USER_ID_ARG}",
            arguments = listOf(navArgument(USER_ID_ARG) { type = NavType.LongType }),
        ) {
            UserDetailScreen(
                onEditShowsClick = { navController.navigateToEditShows(it) },
            )
        }
        composable(
            "editShows/{$USER_ID_ARG}",
            arguments = listOf(navArgument(USER_ID_ARG) { type = NavType.LongType }),
        ) {
            EditShowsScreen(navController = navController)
        }
    }
}

fun NavHostController.navigateToUserDetail(user: User) {
    navigate("user/${user.id}")
}

fun NavHostController.navigateToEditShows(user: User) {
    navigate("editShows/${user.id}")
}

fun NavHostController.navigateToMainScreen() {
    navigate("users")
}
