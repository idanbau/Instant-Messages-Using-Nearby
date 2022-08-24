package com.idanrayan.instantmessagesusingnearby.domain.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.idanrayan.instantmessagesusingnearby.BuildConfig
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File


object FileUtils {
    fun openFile(file: File, activity: Activity) {
        var isCacheFile = false

        MediaScannerConnection.scanFile(
            activity, arrayOf(file.absolutePath), null
        ) { _, u ->
            val uri = try {
                Log.i("Open File", u.path.toString())
                u
            } catch (e: NullPointerException) {
                isCacheFile = true
                FileProvider.getUriForFile(
                    activity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file
                )
            }
            if (file.extension == "apk") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!activity.packageManager.canRequestPackageInstalls()) {
                        activity.startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                                Uri.parse(String.format("package:%s", activity.packageName))
                            ), 1234
                        )
                    } else {
                        installAPK(activity, uri)
                    }
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    uri,
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                if (!isCacheFile) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun installAPK(activity: Activity, uri: Uri) {
        activity.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    uri,
                    "application/vnd.android.package-archive"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
        )
    }


    fun getMimeType(context: Context, uri: Uri): String =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            // If scheme is a content
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))!!
        } else {
            // If scheme is a File
            // This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(uri.path?.let { File(it) }).toString()
            )
        }

    private val okFileImageExtensions = arrayOf(
        "jpg",
        "png",
        "gif",
        "jpeg"
    )

    /**
     * Check if the given file is image
     *
     * @param file
     * @return true if it's image
     */
    fun isImage(file: File): Boolean = file.extension in okFileImageExtensions

    /**
     * Check if the given file is image
     *
     * @param extension
     * @return true if it's image
     */
    fun isImage(extension: String): Boolean =
        extension.replace(".", "") in okFileImageExtensions

    /**
     * Compress the given [file]
     *
     * @param context
     * @param file
     * @return the compressed new file
     */
    suspend fun compressImage(context: Context, file: File): File {
        val result = kotlin.runCatching {
            val f =
                File.createTempFile(
                    "IMG",
                    ".jpeg",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                )
            Compressor.compress(context, file) {
                default()
                destination(f)
            }
            return@runCatching f
        }

        return result.getOrThrow()
    }

}


