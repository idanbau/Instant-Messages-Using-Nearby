package com.idanrayan.instantmessagesusingnearby.domain.utils

import android.app.Activity
import android.content.Context
import android.view.ViewConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat

interface SystemUi {
    val statusBar: Dp
    val navigationBar: Dp
    fun changeIconsMode(light: Boolean = true)
}


@Composable
fun rememberSystemUi(): SystemUi {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    return remember(ctx, density) { SystemUiImpl(ctx, density) }
}

private class SystemUiImpl(private val context: Context, private val density: Density) : SystemUi {
    override fun changeIconsMode(light: Boolean) {
        val window = (context as Activity).window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !light
        }
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun getNavigationBarHeight(): Int {
        val hasMenuKey: Boolean = ViewConfiguration.get(context).hasPermanentMenuKey()
        val resourceId: Int =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0 && !hasMenuKey) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    override val navigationBar: Dp
        get() = getNavigationBarHeight().toDp()

    override val statusBar: Dp
        get() = getStatusBarHeight().toDp()

    private fun Int.toDp(): Dp = with(density) {
        this@toDp.toDp()
    }
}