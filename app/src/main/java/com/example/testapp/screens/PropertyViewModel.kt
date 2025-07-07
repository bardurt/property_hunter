package com.example.testapp.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.testapp.api.BetriHeimApi
import com.example.testapp.api.MeklarinApi
import com.example.testapp.api.OgnApi
import com.example.testapp.api.Query
import com.example.testapp.api.RequestPayload
import com.example.testapp.api.Response
import com.example.testapp.api.RetrofitClient
import com.example.testapp.api.Selection
import com.example.testapp.api.SkiftApi
import com.example.testapp.api.SkynApi
import com.example.testapp.model.PriceRange
import com.example.testapp.model.Property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class PropertyViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState
    var houseHoldIncome = 0.0
    private var displayList: MutableList<Property> = mutableListOf()
    private var totalList: MutableList<Property> = mutableListOf()
    private var propertiesOgn: MutableList<Property> = mutableListOf()
    private var propertiesSkyn: MutableList<Property> = mutableListOf()
    private var propertiesMeklarin: MutableList<Property> = mutableListOf()
    private var propertiesBetri: MutableList<Property> = mutableListOf()
    private var propertiesSkift: MutableList<Property> = mutableListOf()


    private var brokerList: List<String> =
        listOf("All", "Skyn", "Meklarin", "Betri Heim", "Skift", "Ogn")
    private var selectedBroker = "All"
    private var cityList: MutableList<String> = mutableListOf()
    private var selectedCity = "All"
    private var selectedPriceRange: PriceRange = PriceRange.ALL

    fun performAction(action: Action) {
        when (action) {
            Action.LoadUsers -> {
                _viewState.value = _viewState.value.copy(loading = true)
                CoroutineScope(Dispatchers.IO).launch {

                    getHouseHoldIncome()
                    getPropertiesOgn()
                    getPropertiesSkyn()
                    getPropertiesMeklarin()
                    getPropertiesBetri()
                    getPropertiesSkift()
                    for (p in propertiesSkyn) {
                        calculateIncomePriceRatio(houseHoldIncome, p)
                        adjustCostRatioByAge(p)
                    }
                    for (p in propertiesMeklarin) {
                        calculateIncomePriceRatio(houseHoldIncome, p)
                        adjustCostRatioByAge(p)
                    }
                    for (p in propertiesBetri) {
                        calculateIncomePriceRatio(houseHoldIncome, p)
                        adjustCostRatioByAge(p)
                    }
                    for (p in propertiesSkift) {
                        calculateIncomePriceRatio(houseHoldIncome, p)
                        adjustCostRatioByAge(p)
                    }
                    for (p in propertiesOgn) {
                        calculateIncomePriceRatio(houseHoldIncome, p)
                        adjustCostRatioByAge(p)
                    }

                    totalList.addAll(propertiesOgn)
                    totalList.addAll(propertiesSkyn)
                    totalList.addAll(propertiesBetri)
                    totalList.addAll(propertiesMeklarin)
                    totalList.addAll(propertiesSkift)
                    displayList.addAll(totalList)
                    cityList.add("All")
                    val newCities = displayList.map { it.city }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .filterNot { it in cityList }

                    cityList.addAll(newCities)

                    cityList.sort()
                    withContext(Dispatchers.Main) {
                        _viewState.value = _viewState.value.copy(
                            loading = false,
                            properties = displayList,
                            cityFilters = cityList,
                            selectedCity = selectedCity,
                            selectedPriceRange = selectedPriceRange,
                            selectedBroker = selectedBroker,
                            brokerList = brokerList
                        )
                    }
                }
            }

            is Action.FilterCity -> {
                selectedCity = action.city
                applyFilters()
            }

            is Action.FilterPrice -> {
                selectedPriceRange = action.priceRange
                applyFilters()
            }

            is Action.FilterBroker -> {
                selectedBroker = action.broker
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        val cityFiltered = if (selectedCity == null || selectedCity == "All") {
            totalList
        } else {
            totalList.filter { it.city == selectedCity }
        }

        val filtered = if (selectedPriceRange == null) {
            cityFiltered
        } else {
            cityFiltered.filter { property ->
                val price = property.priceIncomeRatio
                selectedPriceRange.contains(price)
            }
        }

        val filterd2 = if (selectedBroker == "All") {
            filtered
        } else {
            filtered.filter { it.broker == selectedBroker }
        }

        _viewState.value = _viewState.value.copy(
            loading = false,
            properties = filterd2,
            selectedCity = selectedCity,
            selectedPriceRange = selectedPriceRange,
            selectedBroker = selectedBroker
        )
    }

    private suspend fun getHouseHoldIncome() {
        val calendar = Calendar.getInstance()
        val previousYear = (calendar.get(Calendar.YEAR) - 1).toString()

        val payload = RequestPayload(
            query = listOf(
                Query("Measure", Selection("item", listOf("12OF12"))),
                Query("Sex", Selection("item", listOf("TOTAL"))),
                Query("Institutional sector", Selection("item", listOf("S1TOT"))),
                Query("Employee's activity", Selection("item", listOf("TOTAL"))),
                Query("Employer's main activity", Selection("item", listOf("TOTAL"))),
                Query("year", Selection("item", listOf(previousYear)))
            ),
            response = Response("json")
        )

        val response = RetrofitClient.instance.getStatistics(payload)

        if (response.isSuccessful) {

            val statResponse = response.body()
            val firstValue = statResponse
                ?.data
                ?.firstOrNull()
                ?.values
                ?.firstOrNull()

            if (firstValue != null) {
                houseHoldIncome = firstValue.toDouble() * 1.5
            } else {
                println("Value not found")
            }
        } else {
            println("API error: ${response.code()} - ${response.errorBody()?.string()}")
        }
    }

    private fun getPropertiesSkyn() {
        val items = try {
            SkynApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        propertiesSkyn.addAll(items)
    }

    private fun getPropertiesMeklarin() {
        val items = try {
            MeklarinApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        propertiesMeklarin.addAll(items)
    }

    private fun getPropertiesBetri() {
        val items = try {
            BetriHeimApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        val filteredItems = items.filterNot {
            it.city.isBlank() && it.address.isBlank() && it.listPrice.isBlank()
        }

        propertiesBetri.addAll(filteredItems)
    }

    private fun getPropertiesSkift() {
        val items = try {
            SkiftApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        propertiesSkift.addAll(items)
    }

    private fun getPropertiesOgn() {
        val items = try {
            OgnApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        propertiesOgn.addAll(items)
    }


    private fun calculateIncomePriceRatio(income: Double, property: Property) {
        val ratio = try {
            property.listPrice.toDouble() / income
        } catch (e: Exception) {
            0
        }

        property.priceIncomeRatio = ratio.toDouble()
    }

    private fun adjustCostRatioByAge(property: Property) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val buildYear = property.buildYear.toIntOrNull()
        val age = if (buildYear != null && buildYear in 1800..currentYear) {
            currentYear - buildYear
        } else {
            1 // default age if year is invalid
        }

        val ageFactor = (age / 10) * 0.2

        property.priceIncomeRatio += ageFactor
    }

    sealed class Action {
        data object LoadUsers : Action()
        data class FilterCity(val city: String) : Action()
        data class FilterPrice(val priceRange: PriceRange) : Action()
        data class FilterBroker(val broker: String) : Action()
    }

    data class ViewState(
        val loading: Boolean = false,
        val properties: List<Property> = listOf(),
        val cityFilters: List<String> = listOf(),
        val selectedCity: String = "All",
        val selectedPriceRange: PriceRange = PriceRange.ALL,
        val selectedBroker: String = "All",
        val brokerList: List<String> = listOf()
    )

}