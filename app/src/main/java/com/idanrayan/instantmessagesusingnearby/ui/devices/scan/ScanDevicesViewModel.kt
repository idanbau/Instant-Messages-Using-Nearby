package com.idanrayan.instantmessagesusingnearby.ui.devices.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.services.NearbyConnections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanDevicesViewModel @Inject constructor(
    private val _dataStoreManager: DataStoreManager,
    private val _nearbyConnections: NearbyConnections
) : ViewModel() {
    private val _isConnectingInProgress = MutableLiveData(false)
    val isConnectingInProgress: LiveData<Boolean> = _isConnectingInProgress

    private val _discoveryState = MutableLiveData(Mode.OFF)
    val discoveryState: LiveData<Mode> = _discoveryState

    val devicesMap = _nearbyConnections.foundDevicesMap

    fun startDiscovery(context: MainActivity) {
        _nearbyConnections.startDiscovery(context) { _discoveryState.value = it }
    }

    fun requestConnection(context: MainActivity, endPointID: String) {
        _isConnectingInProgress.value = true
        viewModelScope.launch {
            _nearbyConnections.requestConnection(
                context,
                endPointID,
                _dataStoreManager.name().first()
            ) {
                _isConnectingInProgress.value = false
            }
        }
    }

    fun stopConnecting(endPointID: String) {
        _nearbyConnections.disconnectConnection(endPointID)
        _isConnectingInProgress.value = false
    }

    fun stopDiscovery() {
        _nearbyConnections.stopDiscovery()
        _discoveryState.value = Mode.OFF
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }
}