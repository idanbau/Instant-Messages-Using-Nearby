package com.idanrayan.instantmessagesusingnearby.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode

@Composable
fun SwitcherBar(title: String, mode: Mode, onChange: (Boolean) -> Unit) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(50.dp)
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title)
            if (mode == Mode.LOADING)
                CircularProgressIndicator(Modifier.size(30.dp))
            else
                Switch(
                    checked = mode == Mode.CONNECTED,
                    onCheckedChange = onChange
                )
        }
    }
}