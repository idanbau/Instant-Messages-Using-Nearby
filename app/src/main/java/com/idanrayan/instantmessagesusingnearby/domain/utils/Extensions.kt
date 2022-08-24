package com.idanrayan.instantmessagesusingnearby.domain.utils

import android.content.Context
import android.text.format.DateFormat
import com.idanrayan.instantmessagesusingnearby.R
import java.io.File
import java.sql.Timestamp
import java.util.*


/**
 * Format the given timestamp to an readable time
 *
 * @param context
 * @return formatted timestamp
 */
fun Timestamp.dateFormatter(context: Context): String {
    val d = Calendar.getInstance()
    d.time = Date(this.time)
    val now = Calendar.getInstance()
    return if (d.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
        DateFormat.format(
            "dd/MM/yyyy",
            d
        ) as String
    } else {
        if (d.get(Calendar.MONTH) != now.get(Calendar.MONTH)) {
            DateFormat.format(
                "dd/MMMM",
                d
            ) as String
        } else {
            when (now.get(Calendar.DAY_OF_MONTH) - d.get(Calendar.DAY_OF_MONTH)) {
                1 -> context.getString(R.string.yesterday)
                0 -> context.getString(R.string.today)
                else -> d.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    Locale.getDefault()
                )!! + "/" + d.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
            }
        }
    } + DateFormat.format(
        " hh:mm",
        d
    ) as String
}

/**
 * Create a new file in the given file
 * The name will be the size of files in the directory and the current date
 * We need the size of files to avoid repetition
 *
 * @param extension
 * @return
 */
fun File.newFile(extension: String): File {
    val file = File(
        this,
        DateFormat.format(
            "yyyy-mm-dd-hh-mm",
            Calendar.getInstance()
        ) as String + "_" + this.list()?.size.toString() + "." + extension
    )
    file.createNewFile()
    return file
}