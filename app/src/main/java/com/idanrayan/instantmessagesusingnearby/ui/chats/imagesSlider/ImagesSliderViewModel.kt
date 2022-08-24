package com.idanrayan.instantmessagesusingnearby.ui.chats.imagesSlider

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImagesSliderViewModel @Inject constructor(
    repo: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    /**
     * The first shown item's index
     *
     */
    val initId = savedStateHandle.get<Long>("initId")

    /**
     * The images that belongs to this conversation
     *
     */
    val images = repo.loadConversationImages(savedStateHandle.get<String>("id")!!)

}