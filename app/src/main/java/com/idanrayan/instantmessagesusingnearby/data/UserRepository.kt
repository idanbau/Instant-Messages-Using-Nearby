package com.idanrayan.instantmessagesusingnearby.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val messagesDao: MessagesDao
) {
    suspend fun getUser(id: String) = userDao.getUser(id)
    fun loadMessages(user_id: String): Flow<List<Message>> = messagesDao.loadMessages(user_id)
    fun loadUsersAndLastMessage() = messagesDao.loadUsersAndLastMessages()
    fun getRegisteredUsers(registeredUsers: List<String>) =
        userDao.getRegisteredUsers(registeredUsers)


    fun loadConversationImages(id: String): Flow<List<Message>> =
        messagesDao.loadConversationImages(id)

    fun loadConversationFiles(id: String): Flow<List<Message>> =
        messagesDao.loadConversationFiles(id)

    suspend fun insertMessage(message: Message): Long = messagesDao.insert(message)

    suspend fun searchMessages(id: String, pattern: String) =
        messagesDao.searchMessages(id, pattern)

    suspend fun deleteMessage(id: Long) = messagesDao.delete(id)

    suspend fun addUser(user: User) = userDao.insert(user)
}