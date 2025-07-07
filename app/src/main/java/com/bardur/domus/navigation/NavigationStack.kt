package com.bardur.domus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bardur.domus.screens.DetailsScreen
import com.bardur.domus.screens.MainScreen
import com.bardur.domus.screens.Screen
import com.bardur.domus.screens.PropertyViewModel

@Composable
fun NavigationStack(
    controller: NavHostController,
    propertyViewModel: PropertyViewModel
) {
    NavHost(navController = controller, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            MainScreen(navController = controller, propertyViewModel)
        }
        composable(route = Screen.Details.route + "?id={id}") { entry ->
            val userId = entry.arguments?.getString("userId").orEmpty()
            DetailsScreen(navController = controller, propertyViewModel, userId)
        }
    }
}
