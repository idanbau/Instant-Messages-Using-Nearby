package com.idanrayan.instantmessagesusingnearby.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.domain.models.MainScreen
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.ui.chats.mainChatConversations.MainChatViewModel
import com.idanrayan.instantmessagesusingnearby.ui.chats.mainChatConversations.MainChatsScreen
import com.idanrayan.instantmessagesusingnearby.ui.devices.devicesGraph
import com.idanrayan.instantmessagesusingnearby.ui.settings.MainSettingsScreen
import com.idanrayan.instantmessagesusingnearby.ui.settings.MainSettingsViewModel

@Composable
fun MainNavGraph(
    context: MainActivity,
    navController: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController,
        startDestination,
        Modifier.padding(innerPadding),
    ) {
        devicesGraph(navController, context)
        navigation(startDestination = Screen.Chats.route, route = "chats") {
            composable(Screen.Chats.route) {
                val mainChatViewModel = hiltViewModel<MainChatViewModel>()
                MainChatsScreen(mainChatViewModel)
            }
        }
        composable(MainScreen.Settings.route) {
            val mainSettingsViewModel = hiltViewModel<MainSettingsViewModel>()
            MainSettingsScreen(mainSettingsViewModel)
        }
    }
}