package com.ratewise.shared.domain.usecase

import com.ratewise.shared.domain.model.ExchangeRate
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConvertCurrencyUseCaseTest {
    private val repo = FakeCurrencyRepository(listOf(ExchangeRate("EUR", "USD", 1.25)))

    @Test
    fun convertsUsingRate() = runTest {
        assertEquals(25.0, ConvertCurrencyUseCase(repo)(20.0, "EUR", "USD"))
    }

    @Test
    fun sameCurrencyReturnsAmount() = runTest {
        assertEquals(10.0, ConvertCurrencyUseCase(repo)(10.0, "EUR", "EUR"))
    }

    @Test
    fun missingRateFails() = runTest {
        assertFailsWith<IllegalStateException> { ConvertCurrencyUseCase(repo)(1.0, "EUR", "JPY") }
    }
}
