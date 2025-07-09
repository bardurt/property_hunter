package com.bardur.domus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


@Composable
fun AffordabilityBadge(ratio: Double, size: Int = 12) {
    val color = when {
        ratio < 1.0 ->  Color(0xFF00B411)
        ratio in 1.0..4.0 -> Color(0xFF1DAF27)
        ratio > 4.0 && ratio <= 5.0 -> Color(0xFFC0EF73)
        ratio > 5.0 && ratio <= 6.0 -> Color(0xFFFFA726)
        ratio > 6.0 -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color, shape = CircleShape)
    )
}

@Composable
fun AffordabilityHeatBar(
    ratio: Double,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp
) {
    val gradientColors = listOf(
        Color(0xFF1DAF27), // 1.0 - 4.0 (Dark Green)
        Color(0xFFC0EF73), // 4.0 - 5.0 (Light Green)
        Color(0xFFFFA726), // 5.0 - 6.0 (Orange)
        Color(0xFFE53935)  // > 6.0 (Red)
    )


    Box(modifier = modifier.padding(vertical = 8.dp)) {
        // Heat bar
        Box(
            modifier = Modifier
                .height(barHeight)
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(6.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val clampedRatio = ratio.coerceIn(0.0, 8.0)
            val arrowOffsetPercent = clampedRatio / 8.0f

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = arrowOffsetPercent.toFloat())
                    .wrapContentWidth(Alignment.End)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Black, TriangleShape)
                )
            }
        }
    }
}

object TriangleShape : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
