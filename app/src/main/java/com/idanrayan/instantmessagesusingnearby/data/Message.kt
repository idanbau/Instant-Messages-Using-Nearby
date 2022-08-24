package com.idanrayan.instantmessagesusingnearby.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["device_id"],
            childColumns = ["user_id"],
        )
    ]
)

data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id", index = true) val userID: String,
    val message: String,
    @ColumnInfo(name = "from_me") val fromMe: Boolean,
    val time: Timestamp = Timestamp(System.currentTimeMillis()),
    val type: String = MessageType.MESSAGE.type
){
    override fun toString(): String = message
}

enum class MessageType(val type: String) {
    MESSAGE("text/plain"), IMAGE("image/"), DOCUMENT("application/octet-stream");
}