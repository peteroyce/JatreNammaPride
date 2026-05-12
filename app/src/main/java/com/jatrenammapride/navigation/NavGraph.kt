package com.jatrenammapride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jatrenammapride.ui.admin.AdminScreen
import com.jatrenammapride.ui.home.HomeScreen
import com.jatrenammapride.ui.lostfound.PostLostItemScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object LostFound : Screen("lost_found")
    object PostLostItem : Screen("post_lost_item")
    object Map : Screen("map")
    object Stories : Screen("stories")
    object Admin : Screen("admin")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.PostLostItem.route) {
            PostLostItemScreen(navController = navController)
        }
        composable(Screen.Admin.route) {
            AdminScreen(navController = navController)
        }
    }
}
