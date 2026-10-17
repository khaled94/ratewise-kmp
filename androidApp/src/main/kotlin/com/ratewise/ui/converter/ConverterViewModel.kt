package com.ratewise.ui.converter

import androidx.lifecycle.ViewModel
import com.ratewise.shared.domain.usecase.ConvertCurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ConverterUiState(
    val amount: String = "1",
    val from: String = "EUR",
    val to: String = "USD",
    val result: Double? = null,
)

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val convertCurrency: ConvertCurrencyUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterUiState())
    val state: StateFlow<ConverterUiState> = _state.asStateFlow()

    // TODO: call convertCurrency when the amount or currencies change.
}
