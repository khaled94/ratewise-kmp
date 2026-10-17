package com.ratewise.ui.converter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // TODO: amount input, currency pickers and converted result
        Text("Converter")
    }
}
