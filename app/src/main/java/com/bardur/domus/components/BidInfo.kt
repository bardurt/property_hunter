package com.bardur.domus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bardur.domus.model.Property
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BidInfo(property: Property) {
    val bidAmount = property.latestBid.replace(".", "").toDoubleOrNull() ?: 0.0
    val isActiveBid = bidAmount > 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isActiveBid) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        if (isActiveBid) {
            Text(
                text = "Active Bidding",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        val price = property.latestBid.toDoubleOrNull()
        val formattedPrice = price?.let {
            NumberFormat.getNumberInstance(Locale.US).format(it)
        }?.replace(",", ".") ?: "N/A"

        Text(
            text = "Latest Bid: $formattedPrice kr.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isActiveBid) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (isActiveBid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Valid Until: ${property.bidValidUntil}.",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
            color = if (isActiveBid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
