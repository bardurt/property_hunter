package com.bardur.domus.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bardur.domus.api.BetriHeimApi
import com.bardur.domus.api.MeklarinApi
import com.bardur.domus.api.OgnApi
import com.bardur.domus.api.Query
import com.bardur.domus.api.RequestPayload
import com.bardur.domus.api.Response
import com.bardur.domus.api.RetrofitClient
import com.bardur.domus.api.Selection
import com.bardur.domus.api.SkiftApi
import com.bardur.domus.api.SkynApi
import com.bardur.domus.model.Constants
import com.bardur.domus.model.PriceRange
import com.bardur.domus.model.Property
import com.bardur.domus.model.PropertyType
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
    private var houseHoldIncome = 0.0
    private var displayList: MutableList<Property> = mutableListOf()
    private var totalList: MutableList<Property> = mutableListOf()

    private val filterAll = "All"
    private var selectedBroker = filterAll
    private var cityList: MutableList<String> = mutableListOf()
    private var selectedCity = filterAll
    private var selectedPriceRange: PriceRange = PriceRange.ALL
    private var filterBid = false

    private var loaded = false

    fun performAction(action: Action) {
        when (action) {
            Action.LoadProperties -> {
                fetch()
            }

            is Action.Refresh -> {
                loaded = false
                fetch()
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

            is Action.FilterBid -> {
                filterBid = action.active
                applyFilters()
            }

            is Action.SelectProperty -> {
                _viewState.value = _viewState.value.copy(
                    selectedProperty = action.property
                )
            }
        }
    }

    private fun applyFilters() {
        val cityFiltered = if (selectedCity == null || selectedCity == filterAll) {
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

        var filterd2 = if (selectedBroker == "All") {
            filtered
        } else {
            filtered.filter { it.broker == selectedBroker }
        }

        var activeBids = ""
        var rejectedBids = ""
        if (filterBid) {
            filterd2 = filterd2.filter { it.hasBid() }
            filterd2 = filterd2.sortedBy { it.isBidRejected() }

            activeBids = filterd2.size.toString()
            rejectedBids = filterd2.filter { it.isBidRejected() }.size.toString()
        }

        _viewState.value = _viewState.value.copy(
            loading = false,
            properties = filterd2,
            selectedCity = selectedCity,
            selectedPriceRange = selectedPriceRange,
            selectedBroker = selectedBroker,
            bidActive = filterBid,
            activeBids = activeBids,
            rejectedBids = rejectedBids
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

        val response = try {
            RetrofitClient.instance.getStatistics(payload)
        } catch (e: Exception) {
            null
        }

        if (response == null) {
            return
        }

        if (response.isSuccessful) {

            val statResponse = response.body()
            val firstValue = statResponse
                ?.data
                ?.firstOrNull()
                ?.values
                ?.firstOrNull()

            if (firstValue != null) {
                houseHoldIncome = firstValue.toDouble() * 1.5
            }
        }
    }

    private fun getPropertiesSkyn() {
        val items = try {
            SkynApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        totalList.addAll(items)
    }

    private fun getPropertiesMeklarin() {
        val items = try {
            MeklarinApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        totalList.addAll(items)
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

        totalList.addAll(filteredItems)
    }

    private fun getPropertiesSkift() {
        val items = try {
            SkiftApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        totalList.addAll(items)
    }

    private fun getPropertiesOgn() {
        val items = try {
            OgnApi.getProperties()
        } catch (e: Exception) {
            Log.e("PropVm", "Error", e)
            listOf()
        }

        totalList.addAll(items)
    }


    private fun calculateIncomePriceRatio(income: Double, property: Property) {
        val ratio = try {
            property.listPrice.toDouble() / income
        } catch (e: Exception) {
            0
        }

        property.priceIncomeRatio = ratio.toDouble()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val buildYear = property.buildYear.toIntOrNull()
        val age = if (buildYear != null && buildYear in 1800..currentYear) {
            currentYear - buildYear
        } else {
            1 // default age if year is invalid
        }

        val ageFactor = getAgeFactor(age)

        property.priceIncomeAgeRatio = ratio.toDouble() + ageFactor

        property.score = (property.priceIncomeAgeRatio + property.priceIncomeRatio) / 2

        if (property.score < 1.0) {
            property.showScore = false
        }

        if (property.score > 10) {
            property.showScore = false
        }

        if (property.city.isEmpty()) {
            property.showScore = false
        }

        if (property.propertyType == PropertyType.Land) {
            property.showScore = false
        }

        if (property.propertyType == PropertyType.Shed) {
            property.showScore = false
        }
    }

    private fun fetch() {
        if (loaded) {
            return
        }

        _viewState.value = _viewState.value.copy(loading = true)
        CoroutineScope(Dispatchers.IO).launch {

            getHouseHoldIncome()
            getPropertiesOgn()
            getPropertiesSkyn()
            getPropertiesMeklarin()
            getPropertiesBetri()
            getPropertiesSkift()

            for (p in totalList) {
                calculateIncomePriceRatio(houseHoldIncome, p)
                calculateIncomeBidRatio(income = houseHoldIncome, property = p)
                calculateFairPrice(income = houseHoldIncome, property = p)
            }

            cityList.add(filterAll)
            for (s in cityList) {
                println(s)
            }
            val newCities = totalList.map { it.city }
                .filter { it.isNotBlank() }
                .distinct()
                .filterNot { it in cityList }

            cityList.addAll(newCities)

            cityList.sort()

            for (s in cityList) {
                println(s)
            }
            loaded = true

            val sortedList = totalList.sortedByDescending {
                it.showScore && it.hasBid() && !it.isBidRejected()
            }

            displayList.addAll(sortedList)
            withContext(Dispatchers.Main) {
                _viewState.value = _viewState.value.copy(
                    loading = false,
                    properties = displayList,
                    cityFilters = cityList,
                    selectedCity = selectedCity,
                    selectedPriceRange = selectedPriceRange,
                    selectedBroker = selectedBroker,
                    brokerList = Constants.brokerList
                )
            }
        }
    }

    private fun calculateIncomeBidRatio(income: Double, property: Property) {
        if (!property.hasBid()) {
            return
        }

        val ratio = try {
            property.latestBid.toDouble() / income
        } catch (e: Exception) {
            0
        }

        property.bidIncomeRatio = ratio.toDouble()

    }

    private fun calculateFairPrice(income: Double, property: Property) {
        val buildYear = property.buildYear.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = if (buildYear != null && buildYear in 1800..currentYear) {
            currentYear - buildYear
        } else {
            1
        }

        if (age in 2..119) {

            val targetRatio = Constants.areaCosts
                .find { it.name == property.city }
                ?.ratio
                ?: 4.0

            val adjustedPrice = calculateAdjustedPrice(
                income = income,
                targetRatio = targetRatio,
                propertyAge = age
            )

            property.fairPrice = adjustedPrice
        }
    }

    private fun calculateAdjustedPrice(
        income: Double,
        targetRatio: Double,
        propertyAge: Int
    ): Double {
        val ageFactor = getAgeFactor(propertyAge)
        val adjustedRatio = targetRatio - ageFactor

        return if (adjustedRatio > 0) {
            adjustedRatio * income
        } else {
            0.0
        }
    }

    private fun getAgeFactor(age: Int): Double {
        return when {
            age < 10 -> 0.1
            age < 20 -> 0.2
            age < 30 -> 0.4
            age < 40 -> 0.8
            age < 70 -> 1.2
            age < 100 -> 1.5
            else -> 2.4
        }
    }

    sealed class Action {
        data object LoadProperties : Action()
        data object Refresh : Action()
        data class FilterCity(val city: String) : Action()
        data class FilterPrice(val priceRange: PriceRange) : Action()
        data class FilterBroker(val broker: String) : Action()
        data class FilterBid(val active: Boolean) : Action()
        data class SelectProperty(val property: Property) : Action()
    }

    data class ViewState(
        val loading: Boolean = false,
        val properties: List<Property> = listOf(),
        val cityFilters: List<String> = listOf(),
        val selectedCity: String = "All",
        val selectedPriceRange: PriceRange = PriceRange.ALL,
        val selectedBroker: String = "All",
        val brokerList: List<String> = listOf(),
        val bidActive: Boolean = false,
        val selectedProperty: Property? = null,
        val rejectedBids: String = "",
        val activeBids: String = ""
    )

}