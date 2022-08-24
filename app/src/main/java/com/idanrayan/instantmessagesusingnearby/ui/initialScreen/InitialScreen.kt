package com.idanrayan.instantmessagesusingnearby.ui.initialScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.navigation.compose.hiltViewModel
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.components.ExpandedButton
import com.idanrayan.instantmessagesusingnearby.ui.components.Input
import com.idanrayan.instantmessagesusingnearby.ui.components.LargeTopBar
import com.idanrayan.instantmessagesusingnearby.ui.components.Title
import com.idanrayan.instantmessagesusingnearby.ui.theme.animatedClickable

const val MAXIMUM_NAME_LENGTH = 20

@Composable
fun InitialScreen(
    viewModel: InitialScreenViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val state by viewModel.state.observeAsState()
    Scaffold(
        topBar = {
            LargeTopBar(
                leading = {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_size))
                            .animatedClickable {
                                navController.popBackStack()
                            }
                    )
                }
            )
        }
    ) {
        Column(
            Modifier
                .padding(dimensionResource(R.dimen.normal))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Title(
                if (state!!.name.isEmpty()) stringResource(R.string.enter_your_full_name) else stringResource(
                    R.string.hello_name, state!!.name
                )
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium)))
            Input(
                value = state!!.name,
                onChange = {
                    viewModel.onNameChange(it)
                },
                error = state?.nameError,
                leading = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = stringResource(R.string.profile),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.small_icon))
                    )
                },
                hint = stringResource(R.string.full_name),
                keyboardCapitalization = KeyboardCapitalization.Words
            )
            Spacer(modifier = Modifier.weight(1F))
            ExpandedButton(
                label = stringResource(R.string.continue_statement),
                modifier = Modifier.align(CenterHorizontally)
            ) {
                viewModel(navController)
            }
        }
    }
}
