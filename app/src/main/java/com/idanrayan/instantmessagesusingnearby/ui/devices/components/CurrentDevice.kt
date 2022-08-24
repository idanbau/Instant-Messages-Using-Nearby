package com.idanrayan.instantmessagesusingnearby.ui.devices.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.idanrayan.instantmessagesusingnearby.R

@Composable
fun CurrentDevice(id: String, name: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.small))
            .clickable {
                onClick()
            },
        color = MaterialTheme.colors.surface.copy(alpha = .7F)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.medium)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$id: $name",
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_link),
                contentDescription = stringResource(R.string.link),
                modifier = Modifier.size(dimensionResource(id = R.dimen.small_icon))
            )
        }
    }
}