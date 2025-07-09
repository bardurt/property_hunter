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
    var bidIncomeRatio: Double = 0.0,
    var fairPrice: Double = 0.0,
    var size: String = "",
    val id: String = "",
    var showScore: Boolean = true,
    val propertyType: PropertyType = PropertyType.Unknown
) {
    fun isBidRejected(): Boolean {
        if (latestBid.isNotEmpty()) {
            return bidValidUntil.isEmpty()
        }
        return false
    }

    fun hasBid(): Boolean {
        return latestBid.isNotEmpty()
    }
}