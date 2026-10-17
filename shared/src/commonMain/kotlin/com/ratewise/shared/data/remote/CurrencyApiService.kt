package com.ratewise.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CurrencyApiService(private val client: HttpClient) {

    suspend fun getLatestRates(base: String): RatesResponse =
        client.get("$BASE_URL/latest") { parameter("from", base) }.body()

    suspend fun getCurrencies(): Map<String, String> =
        client.get("$BASE_URL/currencies").body()

    private companion object {
        const val BASE_URL = "https://api.frankfurter.app"
    }
}
