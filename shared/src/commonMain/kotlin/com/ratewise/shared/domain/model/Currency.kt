package com.ratewise.shared.domain.model

data class Currency(
    val code: String,
    val name: String = code,
    val isFavorite: Boolean = false,
)
