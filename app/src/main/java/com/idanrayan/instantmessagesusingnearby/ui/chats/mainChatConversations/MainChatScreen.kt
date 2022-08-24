package com.idanrayan.instantmessagesusingnearby.ui.chats.mainChatConversations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.data.User
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.utils.dateFormatter
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController

@Composable
fun MainChatsScreen(mainChatViewModel: MainChatViewModel) {
    val navController = LocalNavController.current
    val conversationsAndLastMessages by mainChatViewModel.usersAndLastMessages()
        .observeAsState(emptyMap())
    LazyColumn {
        items(conversationsAndLastMessages.toList()) {
            ChatItem(user = it.first, last = it.second) {
                navController.navigate("${Screen.Conversation.route}/${it.first.deviceId}/${it.first.name}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

}


@Composable
private fun ChatItem(user: User, last: Message?, onClick: () -> Unit) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
    ) {
        Icon(
            Icons.Rounded.Person,
            contentDescription = stringResource(R.string.user),
            Modifier.size(50.dp)
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(end = 10.dp)
        ) {
            Column(Modifier.height(IntrinsicSize.Min)) {
                Text(
                    text = "${user.name}: ${user.deviceId}",
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (last != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (last.fromMe) Text("${stringResource(R.string.you)}: ")
                            if (!last.type.startsWith(MessageType.MESSAGE.type)) {
                                Icon(
                                    if (!last.type.startsWith(MessageType.IMAGE.type)) Icons.Default.Attachment
                                    else Icons.Default.Image,
                                    contentDescription = stringResource(R.string.img),
                                    Modifier.padding(end = 5.dp)
                                )
                            }
                            Text(
                                when {
                                    last.type == MessageType.MESSAGE.type -> last.message
                                    last.type.startsWith(MessageType.IMAGE.type) -> context.getString(
                                        R.string.img
                                    )
                                    else -> context.getString(R.string.document)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.weight(1F))
                        last.time.dateFormatter(context).let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.overline
                            )
                        }
                    }

                }
            }
        }
    }
}