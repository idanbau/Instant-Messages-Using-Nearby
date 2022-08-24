package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components

import android.os.Environment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.SubcomposeAsyncImage
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.ui.components.AsyncImageContent
import java.io.File

@Composable
fun Img(modifier: Modifier = Modifier, message: Message) {
    val file = remember(message.message) {
        if (message.type.startsWith(MessageType.IMAGE.type))
            if (!message.fromMe)
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    message.message
                )
            else
                File(message.message)
        else
            null
    }

    if (file != null)
        ImgBody(model = file, modifier = modifier)
}

@Composable
fun ImgBody(modifier: Modifier = Modifier, model: Any) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
    ) {
        AsyncImageContent()
    }

}