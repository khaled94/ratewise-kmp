package com.ratewise.shared.domain.usecase

import com.ratewise.shared.domain.model.ExchangeRate
import com.ratewise.shared.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCurrencyRepository(
    private val rates: List<ExchangeRate> = emptyList(),
) : CurrencyRepository {
    private val favorites = MutableStateFlow<List<String>>(emptyList())

    override suspend fun getRates(base: String): List<ExchangeRate> = rates.filter { it.base == base }

    override fun observeFavorites(): Flow<List<String>> = favorites

    override suspend fun setFavorite(code: String, favorite: Boolean) {
        favorites.value = if (favorite) favorites.value + code else favorites.value - code
    }
}
