package com.idanrayan.instantmessagesusingnearby.ui.chats.media

import android.app.Activity
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.domain.utils.FileUtils
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.FileDetails
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.Img
import com.idanrayan.instantmessagesusingnearby.ui.theme.animatedClickable
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea
import java.io.File

private enum class SenderType {
    SENT,
    ALL,
    RECEIVED
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(mediaViewModel: MediaViewModel = hiltViewModel()) {
    var selectedImg by remember { mutableStateOf<Message?>(null) }
    val context = LocalContext.current
    val types = SenderType.values()
    val messageTypes = arrayOf(MessageType.DOCUMENT, MessageType.IMAGE)
    var sender by remember { mutableStateOf(SenderType.ALL) }
    var messageType by remember { mutableStateOf(MessageType.DOCUMENT) }
    var show by remember { mutableStateOf(false) }
    val messages = mediaViewModel.files.collectAsState(emptyList()).value

    val images = remember(messages, messageType, sender) {
        messages.filter {
            when (sender) {
                SenderType.SENT -> it.fromMe
                SenderType.ALL -> true
                SenderType.RECEIVED -> !it.fromMe
            }
        }.filter {
            it.type.startsWith(messageType.type.substring(0, messageType.type.indexOf("/")))
        }
    }
    var activeIndex by remember { mutableStateOf(1) }
    val navController = LocalNavController.current
    selectedImg?.let {
        Dialog(onDismissRequest = { selectedImg = null }) {
            Img(
                message = it,
                modifier = Modifier.animatedClickable {
                    selectedImg = null
                },
            )
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.safeArea()

            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                TabRow(
                    selectedTabIndex = activeIndex,
                    modifier = Modifier
                        .weight(1F)
                        .widthIn(max = 250.dp)
                        .padding(horizontal = dimensionResource(R.dimen.medium))
                ) {
                    types.forEachIndexed { i, item ->
                        Tab(
                            selected = i == activeIndex,
                            onClick = {
                                activeIndex = i
                                sender = item
                            },
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                when (item.name) {
                                    SenderType.SENT.name -> stringResource(R.string.sent)
                                    SenderType.ALL.name -> stringResource(R.string.all)
                                    SenderType.RECEIVED.name -> stringResource(R.string.received)
                                    else -> ""
                                }
                            )
                        }
                    }
                }
                Box {
                    IconButton(
                        onClick = {
                            show = true
                        },
                    ) {
                        Icon(
                            Icons.Outlined.FilterList,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                    DropdownMenu(expanded = show, onDismissRequest = { show = false }) {
                        messageTypes.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    messageType = it
                                },
                            ) {
                                Text(
                                    if (it.type.startsWith(MessageType.IMAGE.type)) stringResource(R.string.images)
                                    else stringResource(R.string.documents),
                                    modifier = Modifier.padding(end = dimensionResource(R.dimen.normal))
                                )
                                Spacer(Modifier.weight(1F))
                                AnimatedVisibility(it == messageType) {
                                    Icon(
                                        Icons.Outlined.Check,
                                        contentDescription = stringResource(R.string.checked)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.normal)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.normal)),
            contentPadding = PaddingValues(dimensionResource(R.dimen.normal)),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            items(images, key = { it.id }) {
                val file = remember {
                    if (it.type == MessageType.MESSAGE.type) null
                    else {
                        if (!it.fromMe)
                            File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                it.message
                            )
                        else
                            File(it.message)
                    }
                }
                Box(modifier = Modifier.animateItemPlacement()) {
                    if (it.type.startsWith(MessageType.IMAGE.type))
                        Img(Modifier.animatedClickable { selectedImg = it }, it)
                    else FileDetails(
                        modifier = Modifier.animatedClickable {
                            file?.let {
                                FileUtils.openFile(
                                    it,
                                    context as Activity
                                )
                            }
                        },
                        if (file is File) file else File(file.toString())
                    )
                }
            }
        }
    }
}