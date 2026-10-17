package com.ratewise.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ratewise.ui.converter.ConverterScreen
import com.ratewise.ui.favorites.FavoritesScreen
import com.ratewise.ui.home.HomeScreen
import com.ratewise.ui.settings.SettingsScreen

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Destination("home", "Rates", Icons.Filled.Home)
    data object Converter : Destination("converter", "Convert", Icons.Filled.Refresh)
    data object Favorites : Destination("favorites", "Favorites", Icons.Filled.Favorite)
    data object Settings : Destination("settings", "Settings", Icons.Filled.Settings)
}

private val destinations = listOf(
    Destination.Home,
    Destination.Converter,
    Destination.Favorites,
    Destination.Settings,
)

@Composable
fun RateWiseNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Home.route) { HomeScreen() }
            composable(Destination.Converter.route) { ConverterScreen() }
            composable(Destination.Favorites.route) { FavoritesScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}
