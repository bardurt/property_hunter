package com.example.testapp.model

data class Property(
    val address : String = "",
    val city : String = "",
    val url : String = "",
    val image : String = "",
    val buildYear : String = "",
    val latestBid : String = "",
    val bidValidUntil : String = "",
    val listPrice : String = "",
    val broker : String = "",
    var priceIncomeRatio : Double = 0.0,
    val id : String = ""
)