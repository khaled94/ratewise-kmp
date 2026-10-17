package com.ratewise.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _baseCurrency = MutableStateFlow("EUR")
    val baseCurrency: StateFlow<String> = _baseCurrency.asStateFlow()

    // TODO: persist the selection (e.g. DataStore) and share it with Home.
    fun selectBaseCurrency(code: String) = _baseCurrency.update { code }
}
