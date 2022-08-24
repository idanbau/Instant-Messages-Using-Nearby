package com.idanrayan.instantmessagesusingnearby.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

/**
 * Providing the nav controller
 */
val LocalNavController =
    compositionLocalOf<NavHostController> { error("There's no nav controller provided") }

