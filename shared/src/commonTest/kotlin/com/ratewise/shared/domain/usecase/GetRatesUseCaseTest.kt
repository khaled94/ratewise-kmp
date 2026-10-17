package com.ratewise.shared.domain.usecase

import com.ratewise.shared.domain.model.ExchangeRate
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRatesUseCaseTest {
    @Test
    fun returnsRatesForBaseCurrency() = runTest {
        val repo = FakeCurrencyRepository(
            listOf(
                ExchangeRate("EUR", "USD", 1.1),
                ExchangeRate("USD", "EUR", 0.9),
            ),
        )

        val result = GetRatesUseCase(repo)("EUR")

        assertEquals(listOf(ExchangeRate("EUR", "USD", 1.1)), result)
    }
}
