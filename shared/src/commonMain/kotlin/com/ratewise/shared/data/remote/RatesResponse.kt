package com.ratewise.shared.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
)
