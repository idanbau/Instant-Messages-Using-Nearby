package com.idanrayan.instantmessagesusingnearby.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.idanrayan.instantmessagesusingnearby.data.AppDatabase
import com.idanrayan.instantmessagesusingnearby.data.MessagesDao
import com.idanrayan.instantmessagesusingnearby.data.User
import com.idanrayan.instantmessagesusingnearby.data.UserDao
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun buildDatabase(
        @ApplicationContext context: Context,
        provider: Provider<UserDao>
    ) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, Constants.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .addCallback(
            object : RoomDatabase.Callback() {
                /**
                 * Generate a dummy user after the database get created
                 *
                 * @param db
                 */
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        provider.get().insert(User("Me", "Me"))
                    }
                }
            },
        )
        .build()

    @Singleton
    @Provides
    fun provideMessagesDao(db: AppDatabase): MessagesDao = db.messages()

    @Singleton
    @Provides
    fun provideUsersDao(db: AppDatabase): UserDao = db.user()
}