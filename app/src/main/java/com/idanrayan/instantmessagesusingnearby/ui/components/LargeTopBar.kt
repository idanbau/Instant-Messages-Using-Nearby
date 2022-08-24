package com.idanrayan.instantmessagesusingnearby.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea

@Composable
fun LargeTopBar(
    leading: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .safeArea()
            .height(dimensionResource(R.dimen.large_top_bar_height))
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.normal)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()
        Row(
            content = actions,
            verticalAlignment = Alignment.CenterVertically
        )
    }
}