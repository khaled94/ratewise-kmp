package com.ratewise.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ratewise.ui.components.CurrencyAvatar
import com.ratewise.ui.components.CurrencyListItem
import com.ratewise.ui.components.MessageState
import com.ratewise.ui.components.ScreenTitle
import com.ratewise.ui.components.SearchField
import com.ratewise.ui.components.formatRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = remember(state) { state.items }

    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Rates", "Live exchange rates, updated daily")
        BaseCurrencyCard(state.base, state.baseName)
        SearchField(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.rates.isNotEmpty(),
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                state.rates.isEmpty() && state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.rates.isEmpty() && state.error != null -> MessageState(
                    title = "No connection",
                    message = state.error.orEmpty(),
                    actionLabel = "Retry",
                    onAction = viewModel::refresh,
                )
                items.isEmpty() -> MessageState("No matches", "No currency matches \"${state.query}\".")
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(items, key = { it.code }) { item ->
                        CurrencyListItem(
                            code = item.code,
                            name = item.name,
                            trailing = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(formatRate(item.rate), style = MaterialTheme.typography.titleMedium)
                                    IconButton(onClick = { viewModel.toggleFavorite(item.code) }) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                                            tint = if (item.isFavorite) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BaseCurrencyCard(base: String, baseName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CurrencyAvatar(base)
            Column {
                Text("1 $base equals", style = MaterialTheme.typography.titleMedium)
                Text(
                    "$baseName - change in Settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
