package com.ratewise.shared.data.repository

import com.ratewise.shared.data.local.FavoriteCurrencyDao
import com.ratewise.shared.data.local.FavoriteCurrencyEntity
import com.ratewise.shared.data.remote.CurrencyApiService
import com.ratewise.shared.domain.model.ExchangeRate
import com.ratewise.shared.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class CurrencyRepositoryImpl(
    private val api: CurrencyApiService,
    private val favoriteDao: FavoriteCurrencyDao,
) : CurrencyRepository {

    override suspend fun getRates(base: String): List<ExchangeRate> =
        api.getLatestRates(base).rates.map { (target, rate) ->
            ExchangeRate(base = base, target = target, rate = rate)
        }

    override fun observeFavorites(): Flow<List<String>> = favoriteDao.observeAll()

    override suspend fun setFavorite(code: String, favorite: Boolean) {
        if (favorite) favoriteDao.insert(FavoriteCurrencyEntity(code)) else favoriteDao.delete(code)
    }
}
