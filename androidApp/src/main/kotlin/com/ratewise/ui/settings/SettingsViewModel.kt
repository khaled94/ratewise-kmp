package com.ratewise.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratewise.data.SettingsStore
import com.ratewise.shared.domain.model.Currency
import com.ratewise.shared.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsStore,
    private val repository: CurrencyRepository,
) : ViewModel() {

    val baseCurrency: StateFlow<String> = settings.baseCurrency

    private val _currencies = MutableStateFlow(FALLBACK_CURRENCIES)
    val currencies: StateFlow<List<Currency>> = _currencies.asStateFlow()

    init {
        viewModelScope.launch {
            // Keep the small offline list if the network is unavailable.
            runCatching { repository.getCurrencies() }
                .onSuccess { _currencies.value = it }
        }
    }

    fun selectBaseCurrency(code: String) = settings.setBaseCurrency(code)

    private companion object {
        val FALLBACK_CURRENCIES = listOf(
            "EUR", "USD", "GBP", "JPY", "CHF", "CAD", "AUD", "CNY", "INR", "TRY",
        ).map { Currency(code = it) }
    }
}
