package com.ratewise.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ratewise.ui.components.CurrencyAvatar
import com.ratewise.ui.components.CurrencyPickerDialog
import com.ratewise.ui.components.ScreenTitle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val base by viewModel.baseCurrency.collectAsStateWithLifecycle()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    val baseName = currencies.firstOrNull { it.code == base }?.name ?: base

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ScreenTitle("Settings")

        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Base currency", style = MaterialTheme.typography.titleSmall)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CurrencyAvatar(base)
                    Column {
                        Text(base, style = MaterialTheme.typography.titleLarge)
                        Text(baseName, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Text(
                "Rates on the Rates and Favorites screens are shown against this currency.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text("About", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            Text("RateWise 1.0", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Exchange rates from the Frankfurter API (European Central Bank reference rates, updated on working days).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (showPicker) {
        CurrencyPickerDialog(
            title = "Base currency",
            currencies = currencies,
            selected = base,
            onSelect = {
                viewModel.selectBaseCurrency(it)
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }
}
