import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bardur.domus.components.AffordabilityBadge
import com.bardur.domus.components.BidInfo
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

