package com.bardur.domus.model

data class Property(
    val address: String = "",
    val city: String = "",
    val url: String = "",
    val image: String = "",
    val buildYear: String = "",
    val latestBid: String = "",
    val bidValidUntil: String = "",
    val listPrice: String = "",
    val broker: String = "",
    var priceIncomeRatio: Double = 0.0,
    var priceIncomeAgeRatio: Double = 0.0,
    var score: Double = 0.0,
    var biddingActive: Boolean = false,
    var bidIncomeRatio: Double = 0.0,
    var fairPrice: Double = 0.0,
    var size: String = "",
    val id: String = ""
)