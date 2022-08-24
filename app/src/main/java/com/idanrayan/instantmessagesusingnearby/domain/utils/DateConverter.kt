package com.idanrayan.instantmessagesusingnearby.domain.utils

import androidx.room.TypeConverter
import java.sql.Timestamp

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Timestamp = Timestamp(value)

    @TypeConverter
    fun dateToTimestamp(date: Timestamp): Long = date.time
}