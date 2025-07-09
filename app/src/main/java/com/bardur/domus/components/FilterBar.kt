package com.bardur.domus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.bardur.domus.model.PriceRange



@Composable
fun FilterBar(
    cities: List<String>,
    selectedCity: String?,
    onCitySelected: (String) -> Unit,
    brokers: List<String>,
    selectedBroker: String?,
    onBrokerSelected: (String) -> Unit,
    selectedPriceRange: PriceRange,
    onPriceRangeSelected: (PriceRange) -> Unit,
    bidStatus: Boolean,
    onBidSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                DropdownMenuFilter(
                    label = "City",
                    options = cities,
                    selectedOption = selectedCity,
                    onOptionSelected = { onCitySelected(it) },
                    optionToString = { it },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                DropdownMenuFilter(
                    label = "Broker",
                    options = brokers,
                    selectedOption = selectedBroker,
                    onOptionSelected = { onBrokerSelected(it.toString()) },
                    optionToString = { it ?: "All" },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp), contentAlignment = Alignment.Center
            ) {
                DropdownMenuFilter(
                    label = "Score",
                    options = PriceRange.entries,
                    selectedOption = selectedPriceRange,
                    onOptionSelected = { onPriceRangeSelected(it ?: PriceRange.ALL) },
                    optionToString = { it.displayName ?: "All" }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp), contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Bids",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ButtonDefaults.textButtonColors().contentColor
                    )
                    Switch(
                        checked = bidStatus,
                        onCheckedChange = { onBidSelected(it) },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        }
    }
}


