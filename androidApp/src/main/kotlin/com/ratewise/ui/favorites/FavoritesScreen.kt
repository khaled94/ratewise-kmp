package com.ratewise.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ratewise.ui.components.CurrencyListItem
import com.ratewise.ui.components.MessageState
import com.ratewise.ui.components.ScreenTitle
import com.ratewise.ui.components.formatRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val items = remember(state) { state.items }

    Column(Modifier.fillMaxSize()) {
        ScreenTitle("Favorites", "Rates against ${state.base} - swipe left to remove")
        if (state.isLoading && items.isNotEmpty()) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        when {
            items.isEmpty() -> MessageState(
                title = "No favorites yet",
                message = "Tap the star next to a currency on the Rates screen to save it here.",
            )
            else -> LazyColumn(Modifier.fillMaxSize()) {
                if (state.error != null) {
                    item {
                        Text(
                            state.error.orEmpty(),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                items(items, key = { it.code }) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.remove(item.code)
                                true
                            } else {
                                false
                            }
                        },
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove ${item.code}")
                            }
                        },
                    ) {
                        Surface {
                            CurrencyListItem(
                                code = item.code,
                                name = item.name,
                                trailing = {
                                    Text(
                                        item.rate?.let { formatRate(it) } ?: "-",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
