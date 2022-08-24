package com.idanrayan.instantmessagesusingnearby.ui.chats.mainChatConversations

import androidx.lifecycle.ViewModel
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    fun usersAndLastMessages() = userRepository.loadUsersAndLastMessage()
}