package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.ui.components.Input
import com.idanrayan.instantmessagesusingnearby.ui.theme.animatedClickable

@Composable
fun SendMessageBar(
    messageValue: String,
    isConnected: Boolean,
    onMessageChanged: (String) -> Unit,
    onUploadClicked: () -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.medium),
                bottom = dimensionResource(id = R.dimen.medium)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isConnected)
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onUploadClicked
                ) {
                    Icon(
                        Icons.Default.FileUpload,
                        contentDescription = stringResource(id = R.string.upload_file),
                    )
                }
                Input(
                    value = messageValue,
                    onChange = onMessageChanged,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = dimensionResource(id = R.dimen.normal)),
                    action = {
                        AnimatedVisibility(
                            visible = messageValue.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_upload),
                                contentDescription = stringResource(R.string.send),
                                modifier = Modifier.animatedClickable(onSend)
                            )
                        }
                    },
                    singleLine = false,
                    hint = stringResource(R.string.type_something),
                    keyboardCapitalization = KeyboardCapitalization.Sentences
                )
            }
        else
            Text(stringResource(R.string.there_is_no_connection))
    }
}
