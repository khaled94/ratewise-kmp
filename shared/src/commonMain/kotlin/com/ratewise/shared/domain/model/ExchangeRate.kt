package com.ratewise.shared.domain.model

data class ExchangeRate(
    val base: String,
    val target: String,
    val rate: Double,
)
