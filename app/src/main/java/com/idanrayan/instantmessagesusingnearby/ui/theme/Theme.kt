package com.idanrayan.instantmessagesusingnearby.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController

private val DarkColorPalette = darkColors(
    surface = DarkBlue,
    onSurface = Color.White,
    primary = Color.Black,
    onPrimary = Color.White,
    onSecondary = DarkRed,
)

private val LightColorPalette = lightColors(
    primary = Color.Black,
    surface = LightBlue,
    onPrimary = Color.White,
    secondary = Gray200,
    onSecondary = Claret
)

@Composable
fun InstantMessagesUsingNearbyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    CompositionLocalProvider(LocalNavController provides rememberNavController()) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}