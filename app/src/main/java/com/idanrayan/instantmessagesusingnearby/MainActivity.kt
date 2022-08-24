package com.idanrayan.instantmessagesusingnearby

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.luminance
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.compose.NavHost
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.utils.rememberSystemUi
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
        window.decorView.adjustBottomNavigationBar()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContent {
            var initScreen by rememberSaveable { mutableStateOf<String?>(null) }
            InstantMessagesUsingNearbyTheme {
                val colors = MaterialTheme.colors
                val systemUi = rememberSystemUi()
                LaunchedEffect(initScreen) {
                    initScreen =
                        if (dataStoreManager.onBoard.first()) Screen.OnBoarding.route else Screen.ChatNavigation.route
                }
                Scaffold {
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
                LaunchedEffect(colors) {
                    systemUi.changeIconsMode(colors.background.luminance() < .5F)
                }
            }
        }
    }

    private fun View.adjustBottomNavigationBar() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(
                bottom = if (ime <= 0) windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom else ime
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}
