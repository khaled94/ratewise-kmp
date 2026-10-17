package com.ratewise.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratewise.data.SettingsStore
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

data class FavoriteItem(val code: String, val name: String, val rate: Double?)

data class FavoritesUiState(
    val base: String = "EUR",
    val favorites: List<String> = emptyList(),
    val names: Map<String, String> = emptyMap(),
    val rates: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val items: List<FavoriteItem>
        get() = favorites.map { code ->
            FavoriteItem(code, names[code] ?: code, if (code == base) 1.0 else rates[code])
        }
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getRates: GetRatesUseCase,
    private val repository: CurrencyRepository,
    private val settings: SettingsStore,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState(base = settings.baseCurrency.value))
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            settings.baseCurrency.collect { base ->
                _state.update { it.copy(base = base, rates = emptyMap()) }
                refresh()
            }
        }
        viewModelScope.launch {
            repository.observeFavorites().collect { codes ->
                _state.update { it.copy(favorites = codes) }
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
                names to getRates(base).associate { it.target to it.rate }
            }.onSuccess { (names, rates) ->
                _state.update { it.copy(isLoading = false, names = names, rates = rates) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                _state.update { it.copy(isLoading = false, error = "Couldn't load rates for your favorites.") }
            }
        }
    }

    fun remove(code: String) {
        viewModelScope.launch { repository.setFavorite(code, false) }
    }
}
