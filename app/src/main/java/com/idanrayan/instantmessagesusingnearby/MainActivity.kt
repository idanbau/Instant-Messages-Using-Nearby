package com.idanrayan.instantmessagesusingnearby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.chats.chatsGraph
import com.idanrayan.instantmessagesusingnearby.ui.onboarding.onBoardingGraph
import com.idanrayan.instantmessagesusingnearby.ui.theme.InstantMessagesUsingNearbyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var initScreen by rememberSaveable { mutableStateOf<String?>(null) }
            InstantMessagesUsingNearbyTheme {
                LaunchedEffect(initScreen) {
                    initScreen =
                        if (dataStoreManager.onBoard.first()) Screen.OnBoarding.route else Screen.ChatNavigation.route
                }
                initScreen?.let { init ->
                    NavHost(
                        navController = LocalNavController.current,
                        startDestination = init
                    ) {
                        onBoardingGraph()
                        chatsGraph()
                    }
                }
            }
        }
    }
}
