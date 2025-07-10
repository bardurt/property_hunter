package com.bardur.domus.screens

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object Details : Screen("details_screen")
}