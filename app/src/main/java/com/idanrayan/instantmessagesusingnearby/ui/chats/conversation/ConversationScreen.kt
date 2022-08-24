package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.services.checkForPermission
import com.idanrayan.instantmessagesusingnearby.domain.services.launchPermissions
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.MessageBox
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.SendMessageBar
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.UploadSheetContent
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConversationScreen(
    conversationViewModel: ConversationViewModel = hiltViewModel()
) {
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val messages by conversationViewModel.getMessages().collectAsState(emptyList())
    val currentMessage by conversationViewModel.currentMessage.observeAsState("")
    val loading =
        conversationViewModel.loading.filter { it.userId == conversationViewModel.deviceId }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    LaunchedEffect(messages.size, loading.size) {
        if (messages.isNotEmpty()) {
            state.scrollToItem(0)
        }
    }
    val showModal = launchPermissions {
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            UploadSheetContent {
                conversationViewModel.uploadFile(it, context)
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
        },
        sheetState = bottomSheetState,
        scrimColor = Color.Black.copy(alpha = 0.32f),
        sheetBackgroundColor = MaterialTheme.colors.background,
    ) {
        Scaffold(
            bottomBar = {
                SendMessageBar(
                    messageValue = currentMessage,
                    onMessageChanged = { conversationViewModel.setMessage(it) },
                    isConnected = conversationViewModel.isConnected(),
                    onUploadClicked = {
                        checkForPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            context,
                            showModal
                        )
                    }
                ) {
                    conversationViewModel.sendMessage(context)
                }
            },
            topBar = {
                TopAppBar(
                    navigationIcon = {
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
                    },
                    actions = {
                        TextButton(onClick = { navController.navigate(Screen.Gallery.route + "/" + conversationViewModel.deviceId) }) {
                            Text(
                                stringResource(
                                    R.string.media,
                                ),
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                        IconButton(
                            onClick = {
                                navController.navigate("${Screen.Search.route}/${conversationViewModel.deviceId}")
                            },
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.search),
                            )
                        }
                    },
                    title = {
                        conversationViewModel.deviceName?.let { Text(it) }
                    },
                    modifier = Modifier.safeArea(),
                    contentColor = MaterialTheme.colors.onPrimary

                )
            }
        ) { padding ->
            LazyColumn(
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.normal)),
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(4.5.dp),
                state = state
            ) {
                item {
                    if (loading.isNotEmpty()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = dimensionResource(R.dimen.small)),
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        dimensionResource(R.dimen.small)
                                    )
                                ) {
                                    Text(stringResource(R.string.file_on_way))
                                    Spacer(Modifier.width(10.dp))
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
                items(messages, key = { it.id }) {
                    MessageBox(
                        message = it
                    ) {
                        coroutineScope.launch { conversationViewModel.deleteMessage(it.id) }
                    }
                }
            }

        }
    }
}