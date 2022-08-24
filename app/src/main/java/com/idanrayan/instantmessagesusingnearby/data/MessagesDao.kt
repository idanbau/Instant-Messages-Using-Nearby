package com.idanrayan.instantmessagesusingnearby.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
    @Query("SELECT * FROM users LEFT OUTER JOIN (SELECT * FROM Messages) ON users.device_id = user_id order by time DESC")
    fun loadUsersAndLastMessages(): LiveData<Map<User, Message?>>

    @Query("SELECT * FROM messages WHERE user_id =:user_id ORDER BY time DESC")
    fun loadMessages(user_id: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE user_id =:id AND type NOT LIKE :text_pattern")
    fun loadConversationFiles(
        id: String,
        text_pattern: String = MessageType.MESSAGE.type
    ): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE user_id =:id AND type LIKE 'image/%'")
    fun loadConversationImages(id: String): Flow<List<Message>>

    @Insert
    suspend fun insert(message: Message): Long

    @Query("SELECT * FROM messages WHERE user_id =:user_id AND message LIKE '%' || :pattern || '%' ORDER BY time DESC")
    suspend fun searchMessages(user_id: String, pattern: String): List<Message>


    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun delete(id: Long)
}