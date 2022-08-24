package com.idanrayan.instantmessagesusingnearby.domain.services

import android.app.Application
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.net.toFile
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.idanrayan.instantmessagesusingnearby.BuildConfig
import com.idanrayan.instantmessagesusingnearby.MainActivity
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.data.User
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import com.idanrayan.instantmessagesusingnearby.domain.models.FileDetails
import com.idanrayan.instantmessagesusingnearby.domain.models.LoadingDetails
import com.idanrayan.instantmessagesusingnearby.domain.models.PayloadDetails
import com.idanrayan.instantmessagesusingnearby.domain.models.enums.Mode
import com.idanrayan.instantmessagesusingnearby.domain.use_cases.NotificationUseCase
import com.idanrayan.instantmessagesusingnearby.domain.utils.FileUtils
import com.idanrayan.instantmessagesusingnearby.domain.utils.newFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyConnections @Inject constructor(
    private val connectionsClient: ConnectionsClient,
    private val userRepository: UserRepository,
    private val app: Application,
    private val notify: NotificationUseCase,
    private val dataStoreManager: DataStoreManager
) {
    private val _foundDevicesMap = SnapshotStateMap<String, User>()
    val foundDevicesMap: Map<String, User> = _foundDevicesMap

    private val _loadingFiles = SnapshotStateList<LoadingDetails>()
    val loadingFiles: List<LoadingDetails> = _loadingFiles

    val registeredDevices = BiStateMap(mutableStateMapOf("0" to "Me"))

    private companion object {
        const val TAG = "Nearby"
        val strategy = Strategy.P2P_CLUSTER
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(strategy).build()
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(strategy).build()
    }

    private suspend fun toJson(name: String): String = Gson().toJson(
        User(
            dataStoreManager.uuid.first(),
            name
        )
    )

    suspend fun startAdvertising(
        context: MainActivity,
        name: String,
        onStateChanged: (Mode) -> Unit
    ) {
        onStateChanged(Mode.LOADING)
        connectionsClient.startAdvertising(
            toJson(name),
            BuildConfig.APPLICATION_ID,
            ConnectionLifecycle(
                context,
                userRepository,
                connectionsClient,
                payloadCallback,
                _foundDevicesMap,
                registeredDevices
            ),
            advertisingOptions
        ).addOnSuccessListener {
            onStateChanged(Mode.CONNECTED)

            Log.d(TAG, "Start advertising")
        }
            .addOnFailureListener {
                onStateChanged(Mode.OFF)
                MaterialAlertDialogBuilder(
                    ContextThemeWrapper(
                        context,
                        com.google.android.material.R.style.Theme_MaterialComponents_Dialog
                    )
                )
                    .setTitle("Advertising Failed")
                    .setMessage("Advertising failed: ${it.message}")
                    .setNeutralButton(R.string.ok, null)
                    .show()
                Log.d(TAG, "Failed advertising ${it.message}")
            }
    }

    fun stopAdvertising() =
        connectionsClient.stopAdvertising()

    fun startDiscovery(context: MainActivity, onStateChanged: (Mode) -> Unit) {
        onStateChanged(Mode.LOADING)
        connectionsClient.startDiscovery(
            BuildConfig.APPLICATION_ID,
            mEndpointDiscoveryCallback,
            discoveryOptions
        )
            .addOnSuccessListener {
                onStateChanged(Mode.CONNECTED)
                _foundDevicesMap.clear()
                Log.d(TAG, "start discovery")
            }
            .addOnFailureListener {
                onStateChanged(Mode.OFF)
                MaterialAlertDialogBuilder(
                    ContextThemeWrapper(
                        context,
                        com.google.android.material.R.style.Theme_MaterialComponents_Dialog
                    )
                )
                    .setTitle(context.getString(R.string.scanning_failed))
                    .setMessage(context.getString(R.string.scanning_failed_body))
                    .setNeutralButton(R.string.ok, null)
                    .show()
                Log.d(TAG, "Failed Discovery ${it.message}")
            }
    }

    fun stopDiscovery() =
        connectionsClient.stopDiscovery()

    fun disconnectAllEndPoints() {
        connectionsClient.stopAllEndpoints()
        registeredDevices.clear("0", "Me")
    }

    suspend fun requestConnection(
        context: MainActivity,
        endpointId: String,
        name: String,
        onComplete: () -> Unit
    ) {
        Log.d("DeviceScanning", "Request connection to $endpointId")
        connectionsClient.requestConnection(
            toJson(name),
            endpointId,
            ConnectionLifecycle(
                context,
                userRepository,
                connectionsClient,
                payloadCallback,
                _foundDevicesMap,
                registeredDevices
            ),
        ).addOnCompleteListener { onComplete() }
    }

    fun disconnectConnection(endpointId: String) {
        if (endpointId != "Me") {
            connectionsClient.disconnectFromEndpoint(endpointId)
            registeredDevices.remove(endpointId)
        }
    }

    private val mEndpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(
                endpointId: String,
                discoveredEndpointInfo: DiscoveredEndpointInfo
            ) {
                val device = Gson().fromJson(
                    discoveredEndpointInfo.endpointName,
                    User::class.java
                )
                Log.d(TAG, "find $endpointId, ${device.name}")
                _foundDevicesMap[endpointId] = device
            }

            override fun onEndpointLost(endpointId: String) {
                _foundDevicesMap.remove(endpointId)
                Log.d(TAG, "removed $endpointId")
            }
        }

    /**
     * Send a message to [endpointID]
     *
     * @param endpointID
     * @param message
     */
    fun sendPayload(endpointID: String, message: String): Task<Void> =
        connectionsClient.sendPayload(
            endpointID,
            Payload.fromBytes(message.toByteArray())
        )


    /**
     * Send a [ByteArray] payload to [endpointID]
     *
     * @param endpointID
     * @param uri content uri
     */
    suspend fun sendPayload(
        endpointID: String,
        uri: Uri,
        extension: String,
        isImage: Boolean
    ): Task<Void> {
        val res = kotlin.runCatching {
            val isContentScheme = "content" == uri.scheme
            val file: File = if (isContentScheme) {
                val f = app.cacheDir.newFile(extension)
                app.contentResolver.openInputStream(uri)?.copyTo(FileOutputStream(f))
                f
            } else {
                uri.toFile()
            }
            val f = if (isImage) FileUtils.compressImage(app, file) else file
            val payload =
                // Send the image details first
                if (isImage) Payload.fromBytes(f.readBytes()) else Payload.fromFile(f)
            this.sendPayload(
                endpointID,
                Gson().toJson(
                    PayloadDetails(
                        deviceId = dataStoreManager.uuid.first(),
                        message = "",
                        file = FileDetails(
                            id = payload.id,
                            extension = extension,
                            uri = null
                        )
                    )
                )
            ).addOnSuccessListener {
                // then send the image byte array
                connectionsClient.sendPayload(
                    endpointID,
                    payload
                ).addOnCompleteListener {
                    if (isContentScheme) file.delete()
                    if (isImage) f.delete()
                }
            }
        }
        return res.getOrThrow()
    }

    private val payloadCallback = object : PayloadCallback() {
        private val payloadDetails = mutableListOf<PayloadDetails>()

        /**
         * If there's a new payload
         *
         * @param endpointId
         * @param payload
         */
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            try {
                // If the payload of type ByteArray
                CoroutineScope(Dispatchers.IO).launch {
                    when (payload.type) {
                        Payload.Type.BYTES -> {
                            payload.asBytes()?.let { bytes ->
                                val details =
                                    payloadDetails.firstOrNull { it.file?.id == payload.id }
                                // If there's no previous details "No image expected"
                                if (details == null) {
                                    // Decode the PayloadDetails
                                    val currentPayloadDetails =
                                        Gson().fromJson(String(bytes), PayloadDetails::class.java)
                                    // No image payload id provided
                                    // That's mean this is just a simple message
                                    // If this imgId is not null that's mean the next response will be an image
                                    if (currentPayloadDetails.file != null) {
                                        payloadDetails.add(currentPayloadDetails)
                                    } else {
                                        val message = Message(
                                            id = 0,
                                            currentPayloadDetails.deviceId,
                                            currentPayloadDetails.message,
                                            false
                                        )
                                        val id = userRepository.insertMessage(message)
                                        notify(
                                            message.copy(id = id),
                                            userRepository.getUser(
                                                registeredDevices.get(endpointId) ?: ""
                                            )
                                        )
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            saveBytesFile(details, bytes, payload.id, endpointId)
                                        } catch (e: Exception) {
                                            showToast(e.message)
                                        } finally {
                                            onComplete(details)
                                        }
                                    }
                                }

                            }
                        }
                        Payload.Type.FILE -> {
                            payload.asFile()?.let { file ->
                                val details =
                                    payloadDetails.firstOrNull { it.file?.id == payload.id }
                                details?.let {
                                    payloadDetails[payloadDetails.indexOf(it)] =
                                        it.copy(
                                            file = it.file!!.copy(
                                                uri = file.asUri().toString()
                                            )
                                        )
                                    // Start loading
                                    loading(
                                        it.deviceId,
                                        it.file.id
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showToast(e.message)
            }
        }

        private fun loading(userId: String, payloadId: Long) =
            _loadingFiles.add(
                LoadingDetails(userId, payloadId)
            )

        private fun showToast(error: String?) =
            Toast.makeText(
                app,
                error ?: app.getString(R.string.unexpected_error),
                Toast.LENGTH_SHORT
            ).show()

        private suspend fun saveBytesFile(
            details: PayloadDetails,
            bytes: ByteArray,
            payloadId: Long,
            endpointId: String
        ) {
            kotlin.runCatching {
                // Saving file
                val file =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .newFile(details.file!!.extension)
                val msg = Message(
                    0,
                    details.deviceId,
                    file.name,
                    false,
                    type = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(details.file.extension)
                        ?: MessageType.DOCUMENT.type
                )
                val id = userRepository.insertMessage(msg)
                // Start loading
                loading(details.deviceId, payloadId)
                // write the image bytes to the files
                file.writeBytes(bytes)
                // notification
                notify(
                    msg.copy(id = id), endpointId
                )
            }
        }

        private suspend fun saveFile(details: PayloadDetails) {
            kotlin.runCatching {
                val file =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .newFile(details.file?.extension!!)
                app.contentResolver.openInputStream(Uri.parse(details.file.uri!!))
                    ?.copyTo(FileOutputStream(file))
                val message = Message(
                    0,
                    details.deviceId,
                    file.name,
                    false,
                    type = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(details.file.extension)
                        ?: MessageType.DOCUMENT.type
                )
                userRepository.insertMessage(message)
                onComplete(details)
            }
        }

        private fun onComplete(details: PayloadDetails) {
            _loadingFiles.removeAll { it.payloadId == details.file?.id }
            payloadDetails.remove(details)
        }

        override fun onPayloadTransferUpdate(endpointId: String, p1: PayloadTransferUpdate) {
            if (p1.status != PayloadTransferUpdate.Status.IN_PROGRESS) {
                val details =
                    payloadDetails.firstOrNull { it.file?.id == p1.payloadId }
                details?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (p1.status == PayloadTransferUpdate.Status.SUCCESS) {
                            saveFile(it)
                        } else {
                            onComplete(it)
                        }
                    }
                }
            }
        }
    }
}
