package com.ratewise.shared.domain.usecase

import com.ratewise.shared.domain.repository.CurrencyRepository

class ConvertCurrencyUseCase(private val repository: CurrencyRepository) {
    suspend operator fun invoke(amount: Double, from: String, to: String): Double {
        if (from == to) return amount
        val rate = repository.getRates(from).firstOrNull { it.target == to }?.rate
            ?: error("No rate available for $from -> $to")
        return amount * rate
    }
}
