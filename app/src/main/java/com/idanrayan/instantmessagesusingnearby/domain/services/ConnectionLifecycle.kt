package com.idanrayan.instantmessagesusingnearby.domain.services

import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.gms.nearby.connection.*
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.data.User
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConnectionLifecycle(
    private val context: MainActivity,
    private val userRepository: UserRepository,
    private val connectionsClient: ConnectionsClient,
    private val payloadCallback: PayloadCallback,
    private val foundDevicesMap: MutableMap<String, User>,
    private val registeredDevicesMap: BiStateMap<String, String>
) : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
        val connectionDetails =
            Gson().fromJson(connectionInfo.endpointName, User::class.java)
        foundDevicesMap[endpointId] = connectionDetails
        MaterialAlertDialogBuilder(
            ContextThemeWrapper(
                context,
                R.style.Theme_MaterialComponents_Dialog
            )
        ).setTitle(
            context.getString(
                com.idanrayan.instantmessagesusingnearby.R.string.accept_connection_to,
                connectionDetails.name
            )
        )
            .setMessage(
                context.getString(
                    com.idanrayan.instantmessagesusingnearby.R.string.confirm_the_code_matches_on_both_devices,
                    connectionInfo.authenticationDigits
                )
            )
            .setPositiveButton(context.getString(com.idanrayan.instantmessagesusingnearby.R.string.accept))
            { _, _ ->
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            }
            .setNegativeButton(context.getString(com.idanrayan.instantmessagesusingnearby.R.string.cancel)) { _, _ ->
                connectionsClient.rejectConnection(endpointId)
            }
            .setOnDismissListener { connectionsClient.rejectConnection(endpointId) }
            .show()
    }

    override fun onConnectionResult(
        endpointId: String,
        connectionResolution: ConnectionResolution
    ) {
        val status = connectionResolution.status.statusCode
        if (status == ConnectionsStatusCodes.STATUS_OK) {
            val connectionDetails = foundDevicesMap[endpointId]!!
            registeredDevicesMap[endpointId] = connectionDetails.deviceId
            CoroutineScope(Job() + Dispatchers.IO).launch {
                userRepository.addUser(User(connectionDetails.deviceId, connectionDetails.name))
            }
        }
        val connectionMessage = when (status) {
            ConnectionsStatusCodes.STATUS_OK -> context.getString(
                com.idanrayan.instantmessagesusingnearby.R.string.successfully_connected,
                foundDevicesMap[endpointId]
            )
            ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED ->
                context.getString(
                    com.idanrayan.instantmessagesusingnearby.R.string.connection_rejected,
                    foundDevicesMap[endpointId]
                )
            ConnectionsStatusCodes.STATUS_ERROR ->
                context.getString(
                    com.idanrayan.instantmessagesusingnearby.R.string.connection_error,
                    foundDevicesMap[endpointId]
                )
            ConnectionsStatusCodes.TIMEOUT ->
                context.getString(
                    com.idanrayan.instantmessagesusingnearby.R.string.connection_timeout,
                    foundDevicesMap[endpointId]
                )
            else -> context.getString(
                com.idanrayan.instantmessagesusingnearby.R.string.something_wrong_happened,
                status
            )
        }
        MaterialAlertDialogBuilder(
            ContextThemeWrapper(
                context,
                R.style.Theme_MaterialComponents_Dialog
            )
        ).setTitle(
            if (status == ConnectionsStatusCodes.STATUS_OK) context.getString(com.idanrayan.instantmessagesusingnearby.R.string.connection_success) else context.getString(
                com.idanrayan.instantmessagesusingnearby.R.string.connection_failed
            )
        )
            .setMessage(connectionMessage)
            .setNeutralButton(com.idanrayan.instantmessagesusingnearby.R.string.ok)
            { _, _ -> }
            .show()
    }

    override fun onDisconnected(endpointId: String) {
        registeredDevicesMap.remove(endpointId)
    }
}