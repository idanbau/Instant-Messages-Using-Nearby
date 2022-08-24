package com.idanrayan.instantmessagesusingnearby.ui.main

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.models.MainScreen
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea

@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val items = listOf(
        MainScreen.Devices,
        MainScreen.Chats,
        MainScreen.Settings
    )
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.safeArea(),
            ) {
                Text(stringResource(R.string.app_name))
            }
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        MainNavGraph(
            LocalContext.current as MainActivity,
            navController,
            MainScreen.Chats.route,
            innerPadding
        )
    }
}