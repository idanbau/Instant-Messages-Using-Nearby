package com.idanrayan.instantmessagesusingnearby.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class User(
    @PrimaryKey @ColumnInfo(name = "device_id") val deviceId: String,
    @ColumnInfo(name = "name") val name: String
) {
    override fun toString(): String = """
        device id:$deviceId] name:$name
        """
}