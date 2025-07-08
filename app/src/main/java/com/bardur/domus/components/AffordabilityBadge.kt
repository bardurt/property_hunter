package com.bardur.domus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun AffordabilityBadge(ratio: Double, size: Int = 12) {
    val color = when {
        ratio < 1.0 -> Color.Gray

        ratio in 1.0..4.0 -> Color(0xFF388E3C) // Dark Green

        ratio > 4.0 && ratio <= 5.0 -> Color(0xFFB1DA6B) // Light Green

        ratio > 5.0 && ratio <= 6.0 -> Color(0xFFFFA726) // Orange

        ratio > 6.0 -> Color(0xFFE53935) // Red

        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color, shape = CircleShape)
    )
}