package com.ratewise.ui.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ratewise.ui.components.CurrencyAvatar
import com.ratewise.ui.components.CurrencyPickerDialog
import com.ratewise.ui.components.HeroCard
import com.ratewise.ui.theme.BrandEmerald
import com.ratewise.ui.components.ScreenTitle
import com.ratewise.ui.components.formatRate

private enum class Picker { From, To }

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var picker by remember { mutableStateOf<Picker?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ScreenTitle("Converter", "Convert between any two currencies")

        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )

            CurrencySelector("From", state.from) { picker = Picker.From }

            TextButton(onClick = viewModel::swap, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("⇅  Swap currencies")
            }

            CurrencySelector("To", state.to) { picker = Picker.To }

            ResultCard(state, onRetry = viewModel::load)
        }
    }

    picker?.let { which ->
        CurrencyPickerDialog(
            title = if (which == Picker.From) "From currency" else "To currency",
            currencies = state.currencies,
            selected = if (which == Picker.From) state.from else state.to,
            onSelect = { code ->
                if (which == Picker.From) viewModel.onFromChange(code) else viewModel.onToChange(code)
                picker = null
            },
            onDismiss = { picker = null },
        )
    }
}

@Composable
private fun CurrencySelector(label: String, code: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CurrencyAvatar(code)
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(code, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun ResultCard(state: ConverterUiState, onRetry: () -> Unit) {
    HeroCard(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val result = state.result
        when {
            state.error != null -> {
                Text(state.error, style = MaterialTheme.typography.bodyMedium)
                TextButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandEmerald),
                ) { Text("Retry") }
            }
            state.isLoading -> CircularProgressIndicator(color = BrandEmerald, modifier = Modifier.padding(8.dp))
            result == null -> Text("Enter an amount to convert", style = MaterialTheme.typography.bodyLarge)
            else -> {
                Text(
                    "${state.amount} ${state.from} =",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.75f),
                )
                Text(
                    "${formatRate(result)} ${state.to}",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                state.unitRate?.let {
                    Text(
                        "1 ${state.from} = ${formatRate(it)} ${state.to}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandEmerald,
                    )
                }
            }
        }
    }
}
