package com.idanrayan.instantmessagesusingnearby.ui.devices

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.ui.devices.advertising.RememberedDevicesScreen
import com.idanrayan.instantmessagesusingnearby.ui.devices.advertising.RememberedViewModel
import com.idanrayan.instantmessagesusingnearby.ui.devices.scan.ScanDevicesScreen
import com.idanrayan.instantmessagesusingnearby.ui.devices.scan.ScanDevicesViewModel

fun NavGraphBuilder.devicesGraph(navController: NavController, context: MainActivity) {
    navigation(startDestination = "remembered_devices", route = "devices") {
        composable("remembered_devices") {
            val rememberedViewModel = hiltViewModel<RememberedViewModel>()
            RememberedDevicesScreen(
                navController,
                context,
                rememberedViewModel
            )
        }
        composable("scan_devices") {
            val scanDevicesViewModel = hiltViewModel<ScanDevicesViewModel>()
            ScanDevicesScreen(
                context, scanDevicesViewModel
            )
        }
    }
}