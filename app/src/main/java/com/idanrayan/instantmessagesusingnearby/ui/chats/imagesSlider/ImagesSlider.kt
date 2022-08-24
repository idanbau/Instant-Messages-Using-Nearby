package com.idanrayan.instantmessagesusingnearby.ui.chats.imagesSlider

import android.os.Environment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.ui.LocalNavController
import com.idanrayan.instantmessagesusingnearby.ui.components.AsyncImageContent
import com.idanrayan.instantmessagesusingnearby.ui.theme.safeArea
import java.io.File


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ConversationGalleryScreen(
    imagesViewModel: ImagesSliderViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val images = imagesViewModel.images.collectAsState(emptyList()).value
    val index = images.indexOf(images.find { it.id == imagesViewModel.initId })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
                },
                modifier = Modifier.safeArea()
            )
        }
    ) {
        if (index >= 0 && images.isNotEmpty()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                count = images.size,
                state = rememberPagerState(initialPage = index)
            ) {
                val message = images[it]
                SubcomposeAsyncImage(
                    model = if (!message.fromMe)
                        File(
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                            ),
                            message.message
                        )
                    else
                        message.message.toUri(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AsyncImageContent()
                }
            }
        } else
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
    }
}