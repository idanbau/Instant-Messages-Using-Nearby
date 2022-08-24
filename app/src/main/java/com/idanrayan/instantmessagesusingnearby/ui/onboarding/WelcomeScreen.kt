package com.idanrayan.instantmessagesusingnearby.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.components.ExpandedButton
import com.idanrayan.instantmessagesusingnearby.ui.components.Title

@Composable
fun WelcomeScreen() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier= Modifier.weight(1F).background(color = MaterialTheme.colors.background)
        ) {
            Image(
                painter = painterResource(R.drawable.applogo),
                contentDescription = stringResource(R.string.welcome_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(.25F))
            )
        }
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.normal))
                .fillMaxWidth(),
        ) {
            Title(stringResource(R.string.app_name))
            Text(
                stringResource(R.string.welcome_message),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.small),
                    bottom = dimensionResource(R.dimen.large)
                )
            )
            ExpandedButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                label = stringResource(R.string.continue_statement),
                onClick = {
                    navController.navigate(Screen.Initial.route)
                }
            )
        }
    }
}