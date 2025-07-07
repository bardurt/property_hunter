package com.example.testapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PropertyCardShimmer() {
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        )
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for broker and affordability
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(
                    modifier = Modifier
                        .size(width = 100.dp, height = 20.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.weight(1f))
                Spacer(
                    modifier = Modifier
                        .size(width = 140.dp, height = 20.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Image placeholder
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(shimmerBrush, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Address placeholder
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .background(shimmerBrush, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // City placeholder
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .background(shimmerBrush, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row for build year and price
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(
                    modifier = Modifier
                        .size(width = 100.dp, height = 16.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
                Spacer(
                    modifier = Modifier
                        .size(width = 140.dp, height = 16.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Latest bid and valid until placeholders
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(
                    modifier = Modifier
                        .size(width = 120.dp, height = 16.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
                Spacer(
                    modifier = Modifier
                        .size(width = 140.dp, height = 16.dp)
                        .background(shimmerBrush, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
