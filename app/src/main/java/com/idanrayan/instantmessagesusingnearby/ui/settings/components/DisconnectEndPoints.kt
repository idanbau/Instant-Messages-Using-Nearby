package com.idanrayan.instantmessagesusingnearby.ui.settings.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.idanrayan.instantmessagesusingnearby.R

@Composable
fun DisconnectAllEndPoints(onClick: () -> Unit) =
    Button(onClick = onClick) { Text(stringResource(R.string.disconnect_endpoints)) }