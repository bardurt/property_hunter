package com.bardur.domus.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bardur.domus.components.AffordabilityBadge
import com.bardur.domus.model.Property
import java.text.NumberFormat
import java.util.*
import androidx.core.net.toUri

@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: PropertyViewModel,
    id: String
) {
    val viewState by viewModel.viewState.collectAsState()
    val property = viewState.selectedProperty
    property?.let {
        PropertyDetailsContent(
            property = it,
            modifier = Modifier
                .fillMaxSize()
        )
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Property not found.")
        }
    }
}

@Composable
fun PropertyDetailsContent(property: Property, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = property.image,
            contentDescription = property.address,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = property.address,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = property.city,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Broker")
        Text(
            text = property.broker,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Price Information")

        val listPrice = property.listPrice.toDoubleOrNull()
        val formattedPrice = listPrice?.let {
            NumberFormat.getNumberInstance(Locale.US).format(it)
        }?.replace(",", ".") ?: "N/A"

        Text(
            text = "List Price: $formattedPrice kr.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )

        if (property.size.isNotEmpty()) {
            val pricePerMeter = property.listPrice.toDouble() / property.size.toDouble()

            val formattedPricePerMeter = if (pricePerMeter > 0.0) {
                NumberFormat.getNumberInstance(Locale.US)
                    .format(pricePerMeter.toInt())
                    .replace(",", ".")
            } else {
                "N/A"
            }

            Text(
                text = "Price per meter: ${formattedPricePerMeter} kr.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if(property.showScore) {

            val formattedFairPrice = if (property.fairPrice > 0.0) {
                NumberFormat.getNumberInstance(Locale.US)
                    .format(property.fairPrice)
                    .replace(",", ".")
            } else {
                "N/A"
            }

            if (formattedFairPrice.equals("N/A")) {
                Text(
                    text = "Fair Price: $formattedFairPrice",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Text(
                    text = "Fair Price: $formattedFairPrice kr.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }


        if (property.buildYear.isNotEmpty()) {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val buildYear = property.buildYear.toIntOrNull()
            val age = if (buildYear != null && buildYear in 1200..currentYear) {
                currentYear - buildYear
            } else {
                "N/A"
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Build Year",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = buildYear.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Property Age",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$age years",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(property.showScore) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                SectionTitle("Affordability Metrics")

                MetricRow(
                    label = "Price / Household Income",
                    value = property.priceIncomeRatio,
                    badgeValue = property.priceIncomeRatio
                )

                MetricRow(
                    label = "Age-adjusted Price / Household Income",
                    value = property.priceIncomeAgeRatio,
                    badgeValue = property.priceIncomeAgeRatio
                )

                MetricRow(
                    label = "Score",
                    value = property.score,
                    badgeValue = property.score
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (property.latestBid.isNotEmpty()) {
            SectionTitle("Bidding Information")
            BidInfo(property = property)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (property.url.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        val url = property.url
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text("See more")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(bottom = 4.dp)
    )
}

@Composable
fun BidInfo(property: Property) {
    val bidAmount = property.latestBid.replace(".", "").toDoubleOrNull() ?: 0.0
    val listPrice = property.listPrice.replace(".", "").toDoubleOrNull() ?: 0.0

    val isActiveBid = bidAmount > 0.0

    val percentDifference = if (bidAmount > 0 && listPrice > 0) {
        ((bidAmount - listPrice) / listPrice) * 100
    } else {
        0.0
    }

    val formattedDifference = if (listPrice > 0 && bidAmount > 0) {
        val sign = if (percentDifference >= 0) "+" else "-"
        "$sign${
            String.format(
                "%.2f",
                kotlin.math.abs(percentDifference)
            )
        }% ${if (percentDifference >= 0) "above" else "below"} list price"
    } else {
        "N/A"
    }


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

        Spacer(modifier = Modifier.height(8.dp))

        val buyerType = classifyBuyer(bidAmount, listPrice)

        Text(
            text = "Buyer Type: $buyerType",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = when (buyerType) {
                "Aggressive" -> Color(0xFF4CAF50)
                "Passive" -> Color(0xFFFF9800)
                "Emotional" -> Color.Red
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Text(
            text = "Bid Difference: $formattedDifference",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (property.bidIncomeRatio > 0) {
            MetricRow(
                label = "Bid to Income Ratio",
                value = property.bidIncomeRatio,
                badgeValue = property.bidIncomeRatio
            )
        }
    }
}

fun classifyBuyer(bidAmount: Double, listPrice: Double): String {
    if (bidAmount > 0 && listPrice > 0) {
        val percentDiff = ((bidAmount - listPrice) / listPrice) * 100
        return when {
            percentDiff <= -15.0 -> "Lowball"
            percentDiff < -2.0 -> "Passive"
            percentDiff < 5.0 -> "Fair"
            percentDiff < 15.0 -> "Aggressive"
            else -> "Very Aggressive"
        }
    }
    return "Unknown"
}


@Composable
fun MetricRow(
    label: String,
    value: Double?,
    badgeValue: Double? = null,
    badgeSize: Int = 20,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                text = "${String.format("%.2f", value)} $unit",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = "N/A",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (badgeValue != null) {
            Spacer(modifier = Modifier.width(8.dp))
            AffordabilityBadge(badgeValue, size = badgeSize)
        }
    }
}

