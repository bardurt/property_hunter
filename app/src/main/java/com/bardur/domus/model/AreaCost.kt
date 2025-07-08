package com.bardur.domus.model

data class AreaCost(
    val name : String,
    val ratio : Double,
    val pricePerMeterMax : Double = 0.0,
    val pricePerMeterMin : Double = 0.0
)