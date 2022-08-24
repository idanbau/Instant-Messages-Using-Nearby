package com.idanrayan.instantmessagesusingnearby.ui.chats.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    repo: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val files = repo.loadConversationFiles(savedStateHandle.get<String>("id")!!)
}