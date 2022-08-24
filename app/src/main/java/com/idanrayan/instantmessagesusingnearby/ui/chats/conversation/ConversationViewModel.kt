package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.*
import com.google.gson.Gson
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.MessageType
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import com.idanrayan.instantmessagesusingnearby.domain.models.PayloadDetails
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.services.NearbyConnections
import com.idanrayan.instantmessagesusingnearby.domain.utils.Constants
import com.idanrayan.instantmessagesusingnearby.domain.utils.FileUtils
import com.idanrayan.instantmessagesusingnearby.domain.utils.RealPathUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val nearbyConnections: NearbyConnections,
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val loading = nearbyConnections.loadingFiles
    /**
     * The receiver's information
     */

    /**
     * Get device id from the backstack
     */
    val deviceId = savedStateHandle.get<String>("device_id")
    val deviceName = savedStateHandle.get<String>("name")

    /**
     * Get the user id from the backstack
     */
    private val registeredDevices = nearbyConnections.registeredDevices
    fun isConnected() = registeredDevices.containsValue(deviceId ?: "")


    private val _currentMessage = MutableLiveData("")
    val currentMessage: LiveData<String> = _currentMessage


    fun setMessage(message: String) {
        _currentMessage.value = message
    }

    /**
     * Get the messages that belong to this conversation
     *  @return flow of list of messages
     */
    fun getMessages(): Flow<List<Message>> = userRepository.loadMessages(deviceId!!)

    /**
     * Send a message to the user that belongs to this conversation
     * @param context
     */
    fun sendMessage(context: Context) {
        viewModelScope.launch {
            // Send only if the message input is not blank
            if (currentMessage.value!!.isNotBlank()) {
                val id = userRepository.insertMessage(
                    Message(
                        0,
                        deviceId!!,
                        currentMessage.value!!,
                        true
                    )
                )
                // send the payload
                nearbyConnections.sendPayload(
                    registeredDevices.getKey(deviceId)!!,
                    Gson().toJson(
                        PayloadDetails(
                            message = _currentMessage.value!!,
                            deviceId = dataStoreManager.uuid.first()
                        )
                    )
                ).addOnFailureListener {
                    onError(context, id)
                }
                _currentMessage.value = ""
            } else {
                Toast.makeText(
                    context,
                    "Message Too Short",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * If there's an error delete the inserted message and show a toast
     *
     * @param context
     * @param id
     */
    private fun onError(context: Context, id: Long) {
        // Ignore removing message if receiver was me
        if (deviceId != Constants.MY_DEVICE_ID) {
            Toast.makeText(
                context,
                context.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
            viewModelScope.launch {
                userRepository.deleteMessage(id)
            }
        }
    }

    /**
     * Upload the selected image
     *
     * @param uri
     * @param context
     */
    fun uploadFile(uri: Uri, context: Context) {
        val handler = CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            Log.i("ConversationViewModel", e.message.toString())
        }
        viewModelScope.launch(handler + Dispatchers.IO) {
            try {
                val dir = RealPathUtil.getRealPathFromURI(context, uri)
                    ?: copyUriToFile(context, uri)
                val file = File(dir)
                val isImage = FileUtils.isImage(file)
                val id = userRepository.insertMessage(
                    Message(
                        0,
                        deviceId!!,
                        file.path,
                        true,
                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                            ?: MessageType.DOCUMENT.type
                    )
                )
                nearbyConnections.sendPayload(
                    registeredDevices.getKey(deviceId)!!,
                    uri,
                    file.extension,
                    isImage
                ).addOnFailureListener {
                    onError(context, id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun copyUriToFile(context: Context, uri: Uri): String {
        val stream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile(
            "File-",
            ".${FileUtils.getMimeType(context, uri)}",
            context.cacheDir
        )
        stream?.copyTo(file.outputStream())
        return file.path
    }

    suspend fun deleteMessage(id: Long) {
        userRepository.deleteMessage(id)
    }
}

