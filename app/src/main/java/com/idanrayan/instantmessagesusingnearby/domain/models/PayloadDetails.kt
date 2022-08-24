package com.idanrayan.instantmessagesusingnearby.domain.models


/**
 * The payload details
 * The message is inserted only if [file] is null
 *  [deviceId] represent the sender id
 *
 * @property deviceId
 * @property message
 * @property file
 */
data class PayloadDetails(
    val deviceId: String,
    val message: String,
    val file: FileDetails? = null,
)


data class FileDetails(
    val id: Long,
    val extension: String,
    val uri: String?
)
