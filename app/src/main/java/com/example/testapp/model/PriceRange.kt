package com.example.testapp.model

enum class PriceRange(val displayName: String, val range: ClosedFloatingPointRange<Double>) {
    ALL("All", 0.0..Double.MAX_VALUE),
    LOW("Low (1 - 4)", 0.0..4.0),
    MEDIUM("Fair (4 - 6)", 4.0..6.0),
    HIGH("High 6+", 6.0..Double.MAX_VALUE);

    fun contains(value: Double): Boolean {
        return value in range
    }
}