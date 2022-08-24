package com.idanrayan.instantmessagesusingnearby.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import com.idanrayan.instantmessagesusingnearby.domain.utils.rememberSystemUi


fun Modifier.safeArea() = composed {
    val systemUi = rememberSystemUi()
    padding(top = systemUi.statusBar)
}


/**
 * Clickable with scale animation
 *
 * @param onClick
 * @return
 */
fun Modifier.animatedClickable(onClick: () -> Unit): Modifier = composed {
    val selected = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (selected.value) .93f else 1f)
    this
        .scale(scale.value)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    selected.value = true
                    val z = tryAwaitRelease()
                    selected.value = false
                    if (z) {
                        onClick()
                    }
                },
            )
        }
}