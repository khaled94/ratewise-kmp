package com.ratewise.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Persists the user's base currency and shares it between screens. */
@Singleton
class SettingsStore @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _baseCurrency = MutableStateFlow(prefs.getString(KEY_BASE, DEFAULT_BASE) ?: DEFAULT_BASE)
    val baseCurrency: StateFlow<String> = _baseCurrency.asStateFlow()

    fun setBaseCurrency(code: String) {
        prefs.edit().putString(KEY_BASE, code).apply()
        _baseCurrency.value = code
    }

    private companion object {
        const val KEY_BASE = "base_currency"
        const val DEFAULT_BASE = "EUR"
    }
}
