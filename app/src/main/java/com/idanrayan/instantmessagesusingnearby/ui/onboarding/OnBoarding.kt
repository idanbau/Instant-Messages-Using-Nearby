package com.idanrayan.instantmessagesusingnearby.ui.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.ui.initialScreen.InitialScreen

fun NavGraphBuilder.onBoardingGraph() {
    navigation(route = Screen.OnBoarding.route, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) {
            WelcomeScreen()
        }
        composable(Screen.Initial.route) {
            InitialScreen()
        }
    }
}