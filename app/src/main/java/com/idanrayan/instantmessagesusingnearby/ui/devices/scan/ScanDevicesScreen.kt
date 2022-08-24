package com.idanrayan.instantmessagesusingnearby.ui.devices.scan

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.User
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode
import com.idanrayan.instantmessagesusingnearby.domain.services.checkMultiplePermissions
import com.idanrayan.instantmessagesusingnearby.ui.components.LoadingDialog
import com.idanrayan.instantmessagesusingnearby.ui.components.SwitcherBar

@Composable
fun ScanDevicesScreen(
    context: MainActivity,
    viewModel: ScanDevicesViewModel
) {
    val discoveryState by viewModel.discoveryState.observeAsState(Mode.OFF)
    val keyboard = LocalTextInputService.current
    LaunchedEffect(key1 = discoveryState) {
        keyboard?.hideSoftwareKeyboard()
    }

    var endPointID = ""
    val localConnectLaunchPermissions =
        checkMultiplePermissions {
            viewModel.requestConnection(context, endPointID)
        }
    val localConnectLaunchPermissions2 =
        checkMultiplePermissions {
            viewModel.startDiscovery(context)
        }
    if (viewModel.isConnectingInProgress.value == true)
        LoadingDialog {
            viewModel.stopConnecting(endPointID)
        }
    Column {
        SwitcherBar(
            title = stringResource(R.string.scanning_mode, discoveryState.modeName()),
            mode = discoveryState,
            onChange = { v ->
                if (v)
                    localConnectLaunchPermissions2.launch(
                        arrayOf(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                Manifest.permission.BLUETOOTH_SCAN
                            } else "",
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ).filter { it.isNotEmpty() }.toTypedArray()
                    )
                else viewModel.stopDiscovery()
            },
        )
        Divider(thickness = 1.dp)
        DiscoveredDevicesList(devicesList = viewModel.devicesMap) { id ->
            endPointID = id
            localConnectLaunchPermissions.launch(
                arrayOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Manifest.permission.BLUETOOTH_CONNECT
                    } else "",
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    // We need it to prevent IOException on some old devices
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).filter { it.isNotEmpty() }.toTypedArray()
            )
        }
    }
}


@Composable
private fun DiscoveredDevicesList(
    devicesList: Map<String, User>,
    onClick: (String) -> Unit
) {
    LazyColumn {
        devicesList.forEach { (id, device) ->
            item {
                Surface(
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.small))
                        .clickable {
                            onClick(id)
                        },
                    color = MaterialTheme.colors.surface.copy(alpha = .7F)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.medium)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$id: ${device.name}",
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_link),
                            contentDescription = stringResource(R.string.link),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.small_icon))
                        )
                    }
                }
            }
        }
    }
}