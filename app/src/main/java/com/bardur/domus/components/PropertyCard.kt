import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bardur.domus.R
import com.bardur.domus.model.Property
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PropertyCard(
    property: Property,
    onInfoClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = property.broker,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    color = Color.Magenta
                )

                Spacer(Modifier.weight(1f))

                val affordability = if (property.score > 0.0) {
                    String.format("%.2f", property.score)
                } else {
                    "N/A"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Score: $affordability",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 18.sp
                    )

                    if (affordability != "N/A") {
                        AffordabilityBadge(property.score)
                    }

                    IconButton(onClick = { onInfoClick() }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            AsyncImage(
                model = property.image,
                contentDescription = property.address,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = property.address,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = property.city,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (property.buildYear.isNotEmpty()) {
                    Text(
                        text = "Build Year: ${property.buildYear}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val listPrice = property.listPrice.toDoubleOrNull()
                val formattedPrice = listPrice?.let {
                    NumberFormat.getNumberInstance(Locale.US).format(it)
                }?.replace(",", ".") ?: "N/A"

                Text(
                    text = "List Price: $formattedPrice kr.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (property.latestBid.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                BidInfo(property = property)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = { onDetailsClick() }
                ) {
                    Text("Details")
                }
            }
        }
    }
}

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

@Composable
fun InfoCard(count: Int) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Listings found: $count",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )
        }
    }
}


@Composable
fun AffordabilityBadge(ratio: Double, size: Int = 12) {
    val color = when {
        ratio < 1.0 -> Color.Gray

        ratio in 1.0..4.0 -> Color(0xFF388E3C) // Dark Green

        ratio > 4.0 && ratio <= 5.0 -> Color(0xFF81C784) // Light Green

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
