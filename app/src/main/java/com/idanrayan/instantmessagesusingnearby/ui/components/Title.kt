package com.idanrayan.instantmessagesusingnearby.ui.components

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.idanrayan.instantmessagesusingnearby.R

@Composable
fun Title(text: String) =
    Text(
        text,
        style = MaterialTheme.typography.h1,
        modifier = Modifier.widthIn(
            max = dimensionResource(R.dimen.h1_text_max_width)
        )
    )