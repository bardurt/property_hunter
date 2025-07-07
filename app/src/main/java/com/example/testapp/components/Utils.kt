package com.example.testapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testapp.model.PriceRange


@Composable
fun EquationDivision(result: String, numerator: String, denominator: String, constant : String = "") {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$result = ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Fraction(
                numerator = numerator,
                denominator = denominator
            )
            if(constant.isNotEmpty()){
                Text(
                    text = constant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun Fraction(numerator: String, denominator: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = numerator,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Divider(
            modifier = Modifier
                .width(120.dp)
                .height(2.dp),
            color = MaterialTheme.colorScheme.onSurface.copy()
        )
        Text(
            text = denominator,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun <T> DropdownMenuFilter(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionToString: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = selectedOption?.let(optionToString) ?: "All"

    Box {
        TextButton(onClick = { expanded = true }) {
            Text("$label: $selectedText")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(text = optionToString(option)) }
                )
            }
        }
    }
}


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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // City filter dropdown
        DropdownMenuFilter(
            label = "City",
            options = cities,
            selectedOption = selectedCity,
            onOptionSelected = { onCitySelected(it.toString()) },
            optionToString = { it ?: "All" }
        )

        DropdownMenuFilter(
            label = "Broker",
            options = brokers,
            selectedOption = selectedBroker,
            onOptionSelected = { onBrokerSelected(it.toString()) },
            optionToString = { it ?: "All" }
        )

        // Price range filter dropdown
        DropdownMenuFilter(
            label = "Cost Level",
            options = PriceRange.values().toList(),
            selectedOption = selectedPriceRange,
            onOptionSelected = { onPriceRangeSelected(it ?: PriceRange.ALL) },
            optionToString = { it?.displayName ?: "All" }
        )
    }
}

