package com.bardur.domus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bardur.domus.model.Property
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BidInfo(property: Property) {
    val bidAmount = property.latestBid
        .replace(".", "")
        .replace(",", "")
        .toDoubleOrNull() ?: 0.0

    val isActiveBid = bidAmount > 0.0
    val isRejected = property.isBidRejected()

    val backgroundColor = when {
        isRejected -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        isActiveBid -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val textColor = when {
        isRejected -> MaterialTheme.colorScheme.error
        isActiveBid -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val formattedPrice = if (bidAmount > 0.0) {
        NumberFormat
            .getNumberInstance(Locale("fo", "FO"))
            .format(bidAmount)
            .replace(",", ".")
    } else {
        "N/A"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Bid Info",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Bid Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        Spacer(Modifier.height(8.dp))

        if (isActiveBid) {
            if(isRejected){
                Text(
                    text = "Rejected Bid",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            } else {
                Text(
                    text = "Active Bidding",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        } else if (isRejected) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Rejected",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Bid rejected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Latest Bid: $formattedPrice kr.",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )

        if (!isRejected && property.bidValidUntil.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Valid Until: ${property.bidValidUntil}",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        } else {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Status: Rejected",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

