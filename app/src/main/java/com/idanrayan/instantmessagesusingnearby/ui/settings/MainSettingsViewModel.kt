package com.idanrayan.instantmessagesusingnearby.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.services.NearbyConnections
import com.idanrayan.instantmessagesusingnearby.ui.initialScreen.MAXIMUM_NAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val nearbyConnections: NearbyConnections
) :
    ViewModel() {
    val name = dataStoreManager.name()

    fun saveName(name: String) {
        if(name.isNotEmpty() && name.length < MAXIMUM_NAME_LENGTH) {
            viewModelScope.launch {
                dataStoreManager.setName(name)
            }
        }
    }

    fun disconnectEndPoints() = nearbyConnections.disconnectAllEndPoints()
}