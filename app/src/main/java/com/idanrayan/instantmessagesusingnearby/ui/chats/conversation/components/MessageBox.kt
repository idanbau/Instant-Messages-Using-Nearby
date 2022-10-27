package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.utils.FileUtils.openFile
import com.idanrayan.instantmessagesusingnearby.domain.utils.dateFormatter
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import java.io.File


@Composable
fun MessageBox(
    message: Message,
    deleteMessage: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val navController = LocalNavController.current
    MessageAlertBox(
        alert,
        message.type == MessageType.MESSAGE.type,
        dismiss = { alert = false },
        copy = { copyMessage(context, message.toString()) },
        share = { shareMedia(context, message) },
        deleteMessage = { deleteMessage.invoke() })
    val file = remember(message.message) {
        if (message.message == MessageType.MESSAGE.type) null
        else if (!message.fromMe)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                message.message
            ) else File(
                "${Environment.getExternalStorageDirectory()}/Download/${message.message}"
            )
        else
            File(message.message)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.small)),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (message.fromMe) MaterialTheme.colors.surface else MaterialTheme.colors.onSecondary,
                shape = MaterialTheme.shapes.small,
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides if (message.fromMe) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                    Column(
                        modifier = Modifier
                            .padding(
                                horizontal = dimensionResource(R.dimen.medium)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        alert = true
                                    },
                                    onTap = {
                                        when {
                                            message.type == MessageType.MESSAGE.type ->
                                                isExpanded =
                                                    !isExpanded
                                            message.type.startsWith(MessageType.IMAGE.type) -> {
                                                navController.navigate(
                                                    "${Screen.ConversationGallery.route}/${
                                                        navController.currentBackStackEntry?.arguments?.getString(
                                                            "device_id"
                                                        )!!
                                                    }/${message.id}"
                                                )
                                            }
                                            else -> {
                                                file?.let {
                                                    openFile(it, context as Activity)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            .animateContentSize()
                            .padding(1.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                vertical = dimensionResource(R.dimen.small),
                            ),
                        ) {
                            when {
                                message.type == MessageType.MESSAGE.type -> Text(
                                    text = message.toString(),
                                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                message.type.startsWith(MessageType.IMAGE.type) ->
                                    if (file != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(dimensionResource(R.dimen.img_size))
                                        ) {
                                            ImgBody(model = file)
                                        }
                                    }
                                else -> file?.let { FileDetails(file = it) }
                            }
                        }
                    }
                }
            }
            Text(
                text = message.time.dateFormatter(LocalContext.current),
                style = MaterialTheme.typography.overline,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }

    }
}

@Composable
fun FileDetails(modifier: Modifier = Modifier, file: File) {
    val exists = remember(file.path) { file.exists() }
    if (exists) {
        val size = remember(file.path) { (file.length() / 1000).toString() }
        Row(
            modifier
                .height(IntrinsicSize.Max)
                .clip(MaterialTheme.shapes.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(file.extension.uppercase(), style = MaterialTheme.typography.h2)
            Box(
                Modifier
                    .padding(horizontal = dimensionResource(R.dimen.medium))
                    .width(.8.dp)
                    .fillMaxHeight()
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small))
            ) {
                ProvideTextStyle(MaterialTheme.typography.body2) {
                    Text(file.name)
                    Text("${size}kb")
                }
            }
        }
    } else {
        Text(stringResource(R.string.no_such_file_in, file.path))
    }
}

fun copyMessage(context: Context, message: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("Message", message)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(
        context,
        R.string.message_copied,
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
fun MessageAlertBox(
    alert: Boolean,
    isText: Boolean,
    dismiss: () -> Unit,
    copy: () -> Unit,
    share: () -> Unit,
    deleteMessage: () -> Unit
) {
    if (alert)
        AlertDialog(
            onDismissRequest = { dismiss() },
            buttons = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isText) Button(onClick = { copy() }) { Text(stringResource(R.string.copy)) }
                    Button(onClick = { share() }) { Text(stringResource(R.string.share)) }
                    Button(onClick = { deleteMessage() }) { Text(stringResource(R.string.delete)) }
                    Button(onClick = { dismiss() }) { Text(stringResource(R.string.close)) }
                }
            },
        )

}

fun shareMedia(context: Context, message: Message) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        if (message.type == MessageType.MESSAGE.type) putExtra(Intent.EXTRA_TEXT, message.message)
        else putExtra(
            Intent.EXTRA_STREAM, Uri.parse(
                if (message.fromMe) message.message else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        message.message
                    ).toString() else File(
                        "${Environment.getExternalStorageDirectory()}/Download/${message.message}"
                    ).toString()
                }
            )
        )
        type = message.type
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}