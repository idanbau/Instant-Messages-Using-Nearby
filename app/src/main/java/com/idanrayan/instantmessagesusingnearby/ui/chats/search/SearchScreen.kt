package com.idanrayan.instantmessagesusingnearby.ui.chats.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components.MessageBox
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    deviceID: String,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val pattern by searchViewModel.pattern.observeAsState("")
    val navController = LocalNavController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(pattern) {
        searchViewModel.getSearchResults(deviceID)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }, actions = {
                    LaunchedEffect(Unit) {
                        delay(300)
                        focusRequester.requestFocus()
                    }
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1F)
                            .focusRequester(focusRequester),
                        value = pattern,
                        onValueChange = { searchViewModel.onPatternChange(it) },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = MaterialTheme.colors.background,
                            textColor = MaterialTheme.colors.onBackground
                        )
                    )
                },
                title = {},
                modifier = Modifier.safeArea()
            )
        },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.normal),
                vertical = dimensionResource(R.dimen.medium)
            )
        ) {
            if (pattern.isNotBlank() && searchViewModel.searchResults.isEmpty()) {
                item {
                    Box(
                        Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (searchViewModel.isSearching.value) {
                            CircularProgressIndicator()
                        } else {
                            Text(stringResource(R.string.no_results_found))
                        }
                    }
                }
            } else {
                items(searchViewModel.searchResults) {
                    MessageBox(message = it) {
                    }
                }
            }
        }
    }
}