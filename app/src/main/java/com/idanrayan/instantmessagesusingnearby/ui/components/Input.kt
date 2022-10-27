package com.idanrayan.instantmessagesusingnearby.ui.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import com.idanrayan.instantmessagesusingnearby.R


@Composable
fun Input(
    modifier: Modifier = Modifier,
    value: String,
    onChange: (String) -> Unit,
    hint: String = "",
    error: String? = null,
    action: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 5,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small)),
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier
                .sizeIn(minHeight = dimensionResource(id = R.dimen.input_height))
                .fillMaxWidth(),
            value = value,
            onValueChange = onChange,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = MaterialTheme.colors.surface,
                leadingIconColor = MaterialTheme.colors.onSurface,
            ),
            shape = MaterialTheme.shapes.medium,
            placeholder = {
                Text(hint)
            },
            visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = action,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType, capitalization = keyboardCapitalization
            ),
            textStyle = androidx.compose.ui.text.TextStyle(textDirection = TextDirection.Content),
            leadingIcon = leading,
            maxLines = maxLines
        )
        AnimatedVisibility(error != null) {
            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}