package com.example.testapp.screens

// Screen.kt
sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
}