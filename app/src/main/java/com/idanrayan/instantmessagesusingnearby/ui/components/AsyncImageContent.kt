package com.idanrayan.instantmessagesusingnearby.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.SubcomposeAsyncImageScope
import com.idanrayan.instantmessagesusingnearby.R

@Composable
fun SubcomposeAsyncImageScope.AsyncImageContent(onError: () -> Unit = {}) {
    if (painter.state is AsyncImagePainter.State.Error) {
        onError()
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.BrokenImage,
                contentDescription = stringResource(R.string.img_error),
                Modifier.size(50.dp)
            )
        }
    } else {
        SubcomposeAsyncImageContent()
    }
}