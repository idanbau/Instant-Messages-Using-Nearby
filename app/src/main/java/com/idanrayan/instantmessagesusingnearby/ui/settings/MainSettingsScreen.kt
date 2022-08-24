package com.idanrayan.instantmessagesusingnearby.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.idanrayan.instantmessagesusingnearby.ui.settings.components.DisconnectAllEndPoints
import com.idanrayan.instantmessagesusingnearby.ui.settings.components.NameContent

@Composable
fun MainSettingsScreen(mainSettingsViewModel: MainSettingsViewModel) {
    val name by mainSettingsViewModel.name.collectAsState("")
    Column {
        NameContent(name = name, onNameChange = {
            mainSettingsViewModel.saveName(it)
        })
        DisconnectAllEndPoints { mainSettingsViewModel.disconnectEndPoints() }
    }
}

