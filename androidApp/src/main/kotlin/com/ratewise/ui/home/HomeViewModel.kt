package com.ratewise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratewise.data.SettingsStore
import com.ratewise.shared.domain.model.ExchangeRate
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

data class RateItem(
    val code: String,
    val name: String,
    val rate: Double,
    val isFavorite: Boolean,
)

data class HomeUiState(
    val base: String = "EUR",
    val query: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val rates: List<ExchangeRate> = emptyList(),
    val names: Map<String, String> = emptyMap(),
    val favorites: Set<String> = emptySet(),
) {
    val baseName: String get() = names[base] ?: base

    val items: List<RateItem>
        get() {
            val q = query.trim()
            return rates
                .filter { q.isEmpty() || it.target.contains(q, true) || names[it.target]?.contains(q, true) == true }
                .map { RateItem(it.target, names[it.target] ?: it.target, it.rate, it.target in favorites) }
                .sortedBy { it.code }
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRates: GetRatesUseCase,
    private val repository: CurrencyRepository,
    private val settings: SettingsStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(base = settings.baseCurrency.value))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            settings.baseCurrency.collect { base ->
                _state.update { it.copy(base = base, rates = emptyList()) }
                refresh()
            }
        }
        viewModelScope.launch {
            repository.observeFavorites().collect { codes ->
                _state.update { it.copy(favorites = codes.toSet()) }
            }
        }
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val base = _state.value.base
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val names = repository.getCurrencies().associate { it.code to it.name }
                names to getRates(base)
            }.onSuccess { (names, rates) ->
                _state.update { it.copy(isLoading = false, names = names, rates = rates) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                _state.update {
                    it.copy(isLoading = false, error = "Couldn't load rates. Check your connection and try again.")
                }
            }
        }
    }

    fun onQueryChange(query: String) = _state.update { it.copy(query = query) }

    fun toggleFavorite(code: String) {
        viewModelScope.launch {
            repository.setFavorite(code, code !in _state.value.favorites)
        }
    }
}
