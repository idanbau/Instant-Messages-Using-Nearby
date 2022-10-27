package com.idanrayan.instantmessagesusingnearby.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.idanrayan.instantmessagesusingnearby.R
import kotlinx.coroutines.delay

@Composable
fun NameContent(name: String, onNameChange: (String) -> Unit) {
    var openNameDialog by rememberSaveable { mutableStateOf(false) }
    if (openNameDialog) {
        ChangeName(
            onDismiss = {
                openNameDialog = false
            },
            onSave = {
                onNameChange(it)
            },
            name = name
        )
    }

    Text(stringResource(R.string.personal_settings), style = MaterialTheme.typography.h5)
    Surface(modifier = Modifier.clickable
    {
        openNameDialog = true
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Person, contentDescription = "Person")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Name:", style = MaterialTheme.typography.caption)
                    Text(name)
                }
                Icon(Icons.Rounded.Edit, contentDescription = "edit name")
            }
        }
    }
}

@Composable
fun ChangeName(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    name: String
) {
    val currName by rememberSaveable { mutableStateOf(name) }
    var currentName by remember { mutableStateOf(TextFieldValue(currName)) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.change_name))
        },
        text = {
            LaunchedEffect(Unit) {
                delay(300)
                focusRequester.requestFocus()
            }
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            currentName =
                                currentName.copy(selection = TextRange(0, currentName.text.length))
                        }
                    },
                singleLine = true,
                value = currentName,
                onValueChange = { currentName = it },
                label = { Text(stringResource(R.string.name)) },
                textStyle = TextStyle(textDirection = TextDirection.Content)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave.invoke(currentName.text)
                    onDismiss.invoke()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        })
}