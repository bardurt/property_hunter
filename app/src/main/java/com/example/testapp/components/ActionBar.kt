package com.example.testapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ActionBar(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .padding(top = 24.dp)
        .height(48.dp)
        .background(Color.LightGray)
        .fillMaxWidth()) {
        Spacer(Modifier.width(24.dp))
        Text(
            text = "Test App",
            style = MaterialTheme.typography.labelLarge
        )
    }
}