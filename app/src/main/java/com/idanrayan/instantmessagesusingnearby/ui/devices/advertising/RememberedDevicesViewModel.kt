package com.idanrayan.instantmessagesusingnearby.ui.devices.advertising

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.services.NearbyConnections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RememberedViewModel @Inject constructor(
    private val _dataStoreManager: DataStoreManager,
    private val _nearbyConnections: NearbyConnections,
    private val _userRepository: UserRepository
) : ViewModel() {

    private val _advertisingState = MutableLiveData(Mode.OFF)
    val mode: LiveData<Mode> = _advertisingState

    val rememberedDevicesList =
        _nearbyConnections.registeredDevices

    fun startAdvertising(context: MainActivity) =
        viewModelScope.launch {
            val name = _dataStoreManager.name().first()
            _nearbyConnections.startAdvertising(
                context,
                name
            ) { _advertisingState.value = it }
        }

    fun getRegisteredUsers(registeredUsers: List<String>) =
        _userRepository.getRegisteredUsers(registeredUsers)

    fun stopAdvertising() {
        _nearbyConnections.stopAdvertising()
        _advertisingState.value = Mode.OFF
    }

    fun disconnectEndPoint(endPointID: String) =
        _nearbyConnections.disconnectConnection(endPointID)

    override fun onCleared() {
        super.onCleared()
        stopAdvertising()
    }
}