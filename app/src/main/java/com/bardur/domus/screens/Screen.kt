package com.bardur.domus.screens

// Screen.kt
sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object Details : Screen("details_screen")
}