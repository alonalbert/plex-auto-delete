package com.alonalbert.pad.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alonalbert.pad.app.data.User
import com.alonalbert.pad.app.ui.editshows.EditShowsScreen
import com.alonalbert.pad.app.ui.login.LoginScreen
import com.alonalbert.pad.app.ui.navigation.DestinationsArgs.USER_ID_ARG
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.Loading
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.LoggedIn
import com.alonalbert.pad.app.ui.navigation.NavigationViewModel.LoginState.LoggedOut
import com.alonalbert.pad.app.ui.userdetail.UserDetailScreen
import com.alonalbert.pad.app.ui.userlist.UserListScreen

object DestinationsArgs {
    const val USER_ID_ARG = "userId"
}

@Composable
fun MainNavigation() {
    val viewModel: NavigationViewModel = hiltViewModel()

    val navController = rememberNavController()

    val onLoggedIn = {
        viewModel.setLoggedIn(true)
        navController.navigateToMainScreen()
    }

    val onLogout = {
        viewModel.setLoggedIn(false)
        navController.navigateToLogin()
    }

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    val startDestination = when (loginState) {
        Loading -> "loading"
        LoggedIn -> "users"
        LoggedOut -> "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoggedIn = onLoggedIn)
        }
        composable("loading") {
            LoadingScreen()
        }
        composable("users") {
            UserListScreen(
                onLogout = onLogout,
                onUserClick = { navController.navigateToUserDetail(it) },
            )
        }
        composable(
            "user/{$USER_ID_ARG}",
            arguments = listOf(navArgument(USER_ID_ARG) { type = NavType.LongType }),
        ) {
            UserDetailScreen(
                onLogout = onLogout,
                onEditShowsClick = { navController.navigateToEditShows(it) },
            )
        }
        composable(
            "editShows/{$USER_ID_ARG}",
            arguments = listOf(navArgument(USER_ID_ARG) { type = NavType.LongType }),
        ) {
            EditShowsScreen(
                onLogout = onLogout,
                navController = navController,
            )
        }
    }
}

@Composable
private fun LoadingScreen() {

}

private fun NavHostController.navigateToLogin() {
    navigate("login")
}

private fun NavHostController.navigateToUserDetail(user: User) {
    navigate("user/${user.id}")
}

private fun NavHostController.navigateToEditShows(user: User) {
    navigate("editShows/${user.id}")
}

private fun NavHostController.navigateToMainScreen() {
    navigate("users")
}
