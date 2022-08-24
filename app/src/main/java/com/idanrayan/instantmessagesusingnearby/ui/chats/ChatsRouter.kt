package com.idanrayan.instantmessagesusingnearby.ui.chats

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants.CONVERSATION_URI
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.ConversationScreen
import com.idanrayan.instantmessagesusingnearby.ui.chats.imagesSlider.ConversationGalleryScreen
import com.idanrayan.instantmessagesusingnearby.ui.chats.media.MediaScreen
import com.idanrayan.instantmessagesusingnearby.ui.chats.search.SearchScreen
import com.idanrayan.instantmessagesusingnearby.ui.main.MainLayout

fun NavGraphBuilder.chatsGraph() {
    navigation(Screen.Main.route, Screen.ChatNavigation.route) {
        composable(
            Screen.Main.route,
        ) {
            MainLayout()
        }
        composable(
            "${Screen.ConversationGallery.route}/{id}/{initId}",
            arguments = listOf(
                navArgument("initId") { type = NavType.LongType }
            )) {
            ConversationGalleryScreen()
        }
        composable("${Screen.ConversationGallery.route}/{id}") {
            MediaScreen()
        }
        composable(
            "${Screen.Conversation.route}/{device_id}/{name}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "$CONVERSATION_URI/id={device_id}/name={device_name}"
                },
            ),
        ) {
            ConversationScreen()
        }
        composable(
            "${Screen.Search.route}/{id}"
        ) { backStackEntry ->
            SearchScreen(backStackEntry.arguments?.getString("id")!!)
        }
    }
}