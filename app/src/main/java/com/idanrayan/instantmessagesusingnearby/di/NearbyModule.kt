package com.idanrayan.instantmessagesusingnearby.di

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NearbyModule {
    @Singleton
    @Provides
    fun provideNearbyConnections(@ApplicationContext context: Context): ConnectionsClient =
        Nearby.getConnectionsClient(context)
}