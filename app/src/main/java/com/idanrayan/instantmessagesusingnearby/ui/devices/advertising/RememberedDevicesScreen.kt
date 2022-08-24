package com.idanrayan.instantmessagesusingnearby.ui.devices.advertising

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode
import com.idanrayan.instantmessagesusingnearby.domain.services.checkMultiplePermissions
import com.idanrayan.instantmessagesusingnearby.ui.components.SwitcherBar
import com.idanrayan.instantmessagesusingnearby.ui.devices.components.CurrentDevice

@Composable
fun RememberedDevicesScreen(
    navController: NavController,
    context: MainActivity,
    rememberedViewModel: RememberedViewModel
) {
    val advertisingState by rememberedViewModel.mode.observeAsState(Mode.OFF)

    val localLaunchPermissions =
        checkMultiplePermissions {
            if (advertisingState != Mode.CONNECTED)
                rememberedViewModel.startAdvertising(context)
        }
    Column {
        AdvertisingOptions(
            mode = advertisingState,
            onChange = { b ->
                if (b) {
                    localLaunchPermissions.launch(
                        arrayOf(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                Manifest.permission.BLUETOOTH_ADVERTISE else "",
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            // We need it to prevent IOException on some old devices
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
                                Manifest.permission.WRITE_EXTERNAL_STORAGE else ""
                        ).filter { it.isNotEmpty() }.toTypedArray()
                    )
                } else rememberedViewModel.stopAdvertising()
            },
            scanningNavigate = {
                navController.navigate("scan_devices") {
                    launchSingleTop = true
                }
            },
        )
        val users by rememberedViewModel.getRegisteredUsers(rememberedViewModel.rememberedDevicesList.direct.values.toList())
            .observeAsState(
                emptyList()
            )
        LazyColumn {
            items(rememberedViewModel.rememberedDevicesList.direct.values.toList() zip users) {
                CurrentDevice(id = it.first, name = it.second) {
                    rememberedViewModel.disconnectEndPoint(
                        rememberedViewModel.rememberedDevicesList.getKey(
                            it.first
                        )!!
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvertisingOptions(
    mode: Mode,
    onChange: (Boolean) -> Unit,
    scanningNavigate: () -> Unit
) {
    SwitcherBar(
        title = stringResource(R.string.advertising_mode, mode.modeName()),
        mode = mode,
        onChange = onChange
    )
    Divider(thickness = 1.dp)
    Button(onClick = scanningNavigate) {
        Text(stringResource(R.string.scan_for_devices))
    }
}