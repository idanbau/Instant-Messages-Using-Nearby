package com.idanrayan.instantmessagesusingnearby.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.idanrayan.instantmessagesusingnearby.domain.utils.DateConverter


@Database(entities = [User::class, Message::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun user(): UserDao
    abstract fun messages(): MessagesDao
}