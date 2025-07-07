package com.example.testapp.screens

import InfoCard
import PropertyCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.components.EquationDivision
import com.example.testapp.components.FilterBar
import com.example.testapp.components.PropertyCardShimmer
import com.example.testapp.model.Property


@Composable
fun MoreScreen(navController: NavController, viewModel: PropertyViewModel) {
    LaunchedEffect(Unit) {
        viewModel.performAction(PropertyViewModel.Action.LoadUsers)
    }

    var isGridView by rememberSaveable { mutableStateOf(true) }
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
    } else {
        var selectedProperty by remember { mutableStateOf<Property?>(null) }
        FilterBar(
            cities = viewState.cityFilters,
            selectedCity = viewState.selectedCity,
            onCitySelected = { viewModel.performAction(PropertyViewModel.Action.FilterCity(it)) },
            selectedPriceRange = viewState.selectedPriceRange,
            onPriceRangeSelected = { viewModel.performAction(PropertyViewModel.Action.FilterPrice(it)) },
            brokers = viewState.brokerList,
            selectedBroker = viewState.selectedBroker,
            onBrokerSelected = { viewModel.performAction(PropertyViewModel.Action.FilterBroker(it)) }
        )
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
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(properties, key = { it.id }) { property ->
                PropertyCard(property = property, onInfoClick = {
                    selectedProperty = property
                })
            }
        }

        // Show dialog if one is selected
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
            Text(text = "Cost Level Info")
        },
        text = {
            Column {
                Text(
                    text = "The Cost Level is calculated by dividing the house price by the estimated household income."
                )
                Spacer(modifier = Modifier.height(16.dp))
                EquationDivision("Cost Level", "House Price", "Average Salary x 1.5")
            }
        }
    )
}
