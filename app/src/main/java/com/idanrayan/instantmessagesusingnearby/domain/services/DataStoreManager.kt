package com.idanrayan.instantmessagesusingnearby.domain.services

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {
    private companion object {
        val ON_BOARDING_KEY = booleanPreferencesKey("on_boarding")
        val NAME_KEY = stringPreferencesKey("name")
        val UUID_KEY = stringPreferencesKey("uuid")
    }

    private val _settingsDataStore = context.dataStore

    val onBoard: Flow<Boolean> = _settingsDataStore.data
        .map { preferences -> preferences[ON_BOARDING_KEY] ?: true }

    suspend fun setOnBoard(name: String, uuid: String) {
        _settingsDataStore.edit { preferences ->
            preferences[ON_BOARDING_KEY] = false
            preferences[UUID_KEY] = uuid
            preferences[NAME_KEY] = name
        }
    }

    fun name(): Flow<String> = _settingsDataStore.data
        .map { preferences -> preferences[NAME_KEY] ?: "N/A" }

    suspend fun setName(name: String) {
        _settingsDataStore.edit { preferences -> preferences[NAME_KEY] = name }
    }

    val uuid: Flow<String> = _settingsDataStore.data
        .map { preferences -> preferences[UUID_KEY] ?: "N/A" }
}