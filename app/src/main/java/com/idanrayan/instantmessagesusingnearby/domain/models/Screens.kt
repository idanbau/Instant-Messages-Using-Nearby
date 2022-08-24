package com.idanrayan.instantmessagesusingnearby.domain.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.idanrayan.instantmessagesusingnearby.R

sealed class MainScreen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Devices : MainScreen("devices", R.string.find_devices, Icons.Filled.Person)
    object Chats : MainScreen("chats", R.string.chats, Icons.Filled.Chat)
    object Settings : MainScreen("settings", R.string.settings, Icons.Filled.Settings)
}

sealed class Screen(val route: String) {
    object Main : Screen("/")
    object Welcome : Screen("/welcome")
    object Initial : Screen("/init")
    object ChatNavigation : Screen("/chats_navigation")
    object OnBoarding : Screen("/onboarding")
    object Conversation : Screen("/conversation")
    object ConversationGallery : Screen("/conversation-gallery")
    object Gallery : Screen("/conversation-gallery")
    object Chats : Screen("/main-chats")
    object Search : Screen("/search")
}