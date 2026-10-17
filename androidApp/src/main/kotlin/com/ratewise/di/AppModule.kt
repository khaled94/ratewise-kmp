package com.ratewise.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ratewise.shared.data.local.AppDatabase
import com.ratewise.shared.data.local.FavoriteCurrencyDao
import com.ratewise.shared.data.remote.CurrencyApiService
import com.ratewise.shared.data.remote.createHttpClient
import com.ratewise.shared.data.repository.CurrencyRepositoryImpl
import com.ratewise.shared.domain.repository.CurrencyRepository
import com.ratewise.shared.domain.usecase.ConvertCurrencyUseCase
import com.ratewise.shared.domain.usecase.GetRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = createHttpClient()

    @Provides
    @Singleton
    fun provideCurrencyApiService(client: HttpClient): CurrencyApiService = CurrencyApiService(client)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder<AppDatabase>(context, context.getDatabasePath("ratewise.db").absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteCurrencyDao = db.favoriteCurrencyDao()

    @Provides
    @Singleton
    fun provideRepository(api: CurrencyApiService, dao: FavoriteCurrencyDao): CurrencyRepository =
        CurrencyRepositoryImpl(api, dao)

    @Provides
    fun provideGetRatesUseCase(repository: CurrencyRepository) = GetRatesUseCase(repository)

    @Provides
    fun provideConvertCurrencyUseCase(repository: CurrencyRepository) = ConvertCurrencyUseCase(repository)
}
