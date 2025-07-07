package com.example.testapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testapp.screens.MoreScreen
import com.example.testapp.screens.Screen
import com.example.testapp.screens.PropertyViewModel

@Composable
fun NavigationStack(
    controller: NavHostController,
    propertyViewModel: PropertyViewModel
) {
    NavHost(navController = controller, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            MoreScreen(navController = controller, propertyViewModel)
        }
    }
}
