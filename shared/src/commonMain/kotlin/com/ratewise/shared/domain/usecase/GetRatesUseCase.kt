package com.ratewise.shared.domain.usecase

import com.ratewise.shared.domain.model.ExchangeRate
import com.ratewise.shared.domain.repository.CurrencyRepository

class GetRatesUseCase(private val repository: CurrencyRepository) {
    suspend operator fun invoke(base: String): List<ExchangeRate> = repository.getRates(base)
}
