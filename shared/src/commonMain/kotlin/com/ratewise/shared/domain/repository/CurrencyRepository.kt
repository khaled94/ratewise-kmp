package com.ratewise.shared.domain.repository

import com.ratewise.shared.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun getRates(base: String): List<ExchangeRate>
    fun observeFavorites(): Flow<List<String>>
    suspend fun setFavorite(code: String, favorite: Boolean)
}
