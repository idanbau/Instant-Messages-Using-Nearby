package com.idanrayan.instantmessagesusingnearby.ui.chats.conversation.components

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.idanrayan.instantmessagesusingnearby.BuildConfig
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.services.checkForPermission
import com.idanrayan.instantmessagesusingnearby.domain.services.launchPermissions
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UploadSheetContent(
    onFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val uploadImage = uploader(onFileSelected)
    val uploadFile = uploader(onFileSelected, false)
    val takePhoto = takePhoto(onFileSelected)

    ListItem(
        icon = {
            Icon(
                Icons.Outlined.Image,
                contentDescription = stringResource(R.string.image_from_gallery)
            )
        },
        text = {
            Text(stringResource(R.string.open_media_gallery))
        },
        modifier = Modifier.clickable {
            checkForPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                context,
                uploadImage
            )
        }
    )
    ListItem(
        icon = {
            Icon(Icons.Outlined.Camera, contentDescription = stringResource(R.string.take_a_photo))
        },
        text = {
            Text(stringResource(R.string.take_a_photo))
        },
        modifier = Modifier.clickable {
            checkForPermission(
                Manifest.permission.CAMERA,
                context,
                takePhoto
            )
        }
    )
    ListItem(
        icon = {
            Icon(
                Icons.Outlined.FileUpload,
                contentDescription = stringResource(R.string.upload_file)
            )
        },
        text = {
            Text(stringResource(R.string.files))
        },
        modifier = Modifier.clickable {
            checkForPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                context,
                uploadFile
            )
        }
    )
}

@Composable
private fun uploader(
    onFileSelected: (Uri) -> Unit,
    image: Boolean = true
): ManagedActivityResultLauncher<String, Boolean> {
    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let {
            onFileSelected(it)
        }
    }
    return launchPermissions {
        launcher2.launch(arrayOf(if (image) "image/*" else "*/*"))
    }
}

@Composable
private fun takePhoto(onImageUpload: (Uri) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current
    var u: File? = null
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it && u != null) {
                onImageUpload(u!!.toUri())
            }
        }
    return launchPermissions {
        u = File.createTempFile(
            "IMG_",
            ".jpg",
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        )
        launcher.launch(
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider", u!!
            )
        )
    }
}