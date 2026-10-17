package com.ratewise.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratewise.data.SettingsStore
import com.ratewise.shared.domain.model.Currency
import com.ratewise.shared.domain.repository.CurrencyRepository
import com.ratewise.shared.domain.usecase.GetRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConverterUiState(
    val amount: String = "100",
    val from: String = "EUR",
    val to: String = "USD",
    val currencies: List<Currency> = emptyList(),
    val rates: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    /** Price of one unit of [from] expressed in [to], or null while rates are unavailable. */
    val unitRate: Double? get() = if (from == to) 1.0 else rates[to]

    val result: Double? get() = amount.toDoubleOrNull()?.let { amt -> unitRate?.let { amt * it } }
}

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val getRates: GetRatesUseCase,
    private val repository: CurrencyRepository,
    settings: SettingsStore,
) : ViewModel() {

    private val _state = MutableStateFlow(
        settings.baseCurrency.value.let { base ->
            ConverterUiState(from = base, to = if (base == "USD") "EUR" else "USD")
        },
    )
    val state: StateFlow<ConverterUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onAmountChange(text: String) {
        if (text.all { it.isDigit() || it == '.' } && text.count { it == '.' } <= 1) {
            _state.update { it.copy(amount = text) }
        }
    }

    fun onFromChange(code: String) {
        if (code == _state.value.from) return
        _state.update { it.copy(from = code, rates = emptyMap()) }
        load()
    }

    fun onToChange(code: String) = _state.update { it.copy(to = code) }

    fun swap() {
        _state.update { it.copy(from = it.to, to = it.from, rates = emptyMap()) }
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val from = _state.value.from
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val currencies = repository.getCurrencies()
                currencies to getRates(from).associate { it.target to it.rate }
            }.onSuccess { (currencies, rates) ->
                _state.update { it.copy(isLoading = false, currencies = currencies, rates = rates) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                _state.update {
                    it.copy(isLoading = false, error = "Couldn't load rates. Check your connection and try again.")
                }
            }
        }
    }
}
