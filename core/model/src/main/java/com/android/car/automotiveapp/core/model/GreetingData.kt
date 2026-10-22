package com.android.car.automotiveapp.core.model

data class GreetingData(
    val title: String,
    val subtitle: String,
    val architectureHighlights: List<String>,
    val vehicleStatus: String,
    val timestamp: Long
)
