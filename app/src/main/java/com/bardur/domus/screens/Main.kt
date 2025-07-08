package com.bardur.domus.screens

import PropertyCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bardur.domus.components.FilterBar
import com.bardur.domus.components.InfoCard
import com.bardur.domus.components.PropertyCardShimmer
import com.bardur.domus.model.Property


@Composable
fun MainScreen(navController: NavController, viewModel: PropertyViewModel) {
    LaunchedEffect(Unit) {
        viewModel.performAction(PropertyViewModel.Action.LoadUsers)
    }

    val viewStateState = viewModel.viewState.collectAsState()
    val viewState = viewStateState.value
    val properties = viewState.properties

    if (viewState.loading) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            visible = true
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 48.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 700))
                ) {
                    PropertyCardShimmer()
                }
            }
        }
    } else if (properties.isEmpty()) {
        EmptyStateScreen(
            modifier = Modifier.fillMaxSize(),
            onRetry = {
                viewModel.performAction(PropertyViewModel.Action.LoadUsers)
            }
        )
    } else {
        var selectedProperty by remember { mutableStateOf<Property?>(null) }
        var showFilters by rememberSaveable { mutableStateOf(true) }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (showFilters) "Hide Filters" else "Show Filters"
                    )
                }
            }

            AnimatedVisibility(
                visible = showFilters
            ) {
                FilterBar(
                    cities = viewState.cityFilters,
                    selectedCity = viewState.selectedCity,
                    onCitySelected = {
                        viewModel.performAction(
                            PropertyViewModel.Action.FilterCity(
                                it
                            )
                        )
                    },
                    selectedPriceRange = viewState.selectedPriceRange,
                    onPriceRangeSelected = {
                        viewModel.performAction(
                            PropertyViewModel.Action.FilterPrice(
                                it
                            )
                        )
                    },
                    brokers = viewState.brokerList,
                    selectedBroker = viewState.selectedBroker,
                    onBrokerSelected = {
                        viewModel.performAction(
                            PropertyViewModel.Action.FilterBroker(
                                it
                            )
                        )
                    },
                    bidStatus = viewState.bidActive,
                    onBidSelected = { viewModel.performAction(PropertyViewModel.Action.FilterBid(it)) }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    InfoCard(count = properties.size)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(properties, key = { it.id }) { property ->
                    PropertyCard(
                        property = property,
                        onInfoClick = {
                            selectedProperty = property
                        },
                        onDetailsClick = {
                            viewModel.performAction(PropertyViewModel.Action.SelectProperty(property = property))
                            navController.navigate(route = Screen.Details.route + "?id=${property.id}")
                        }
                    )
                }
            }
        }
        selectedProperty?.let { property ->
            PropertyInfoDialog(
                property = property,
                onDismiss = { selectedProperty = null }
            )
        }
    }
}

@Composable
fun PropertyInfoDialog(property: Property, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = {
            Text(text = "Score Info")
        },
        text = {
            Column {
                Text(
                    text = "The property score is calculated to reflect affordability and property age. " +
                            "A lower score generally means a property is more affordable compared to household income."
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Calculation Steps:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("1. Calculate Price / Household Income:")
                Text(
                    text = "   • Ratio = List Price ÷ Income",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("2. Adjust for property age:")
                Text(
                    text = "   • Age Factor = (Age / 10) × 0.2",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "   • Adjusted Ratio = Ratio + Age Factor",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("3. Compute the Score:")
                Text(
                    text = "   • Score = (Adjusted Ratio + Ratio) ÷ 2",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actual numbers for this property:
                Text(
                    text = "For this property:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Price / Income = ${String.format("%.2f", property.priceIncomeRatio)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Age-adjusted Ratio = ${
                        String.format(
                            "%.2f",
                            property.priceIncomeAgeRatio
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Final Score = ${String.format("%.2f", property.score)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}


@Composable
fun EmptyStateScreen(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No properties found.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}
