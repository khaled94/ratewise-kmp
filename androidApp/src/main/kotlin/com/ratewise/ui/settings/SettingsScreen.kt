package com.ratewise.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val base by viewModel.baseCurrency.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // TODO: base currency picker
        Text("Settings - base currency: $base")
    }
}
