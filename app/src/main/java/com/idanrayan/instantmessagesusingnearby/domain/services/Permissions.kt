package com.idanrayan.instantmessagesusingnearby.domain.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.idanrayan.instantmessagesusingnearby.R

@Composable
private fun PermissionDeniedDialog(onDismissRequest: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest,
        title = { Text("Permission Denied") },
        text = { Text("Permission Denied, can't enable") },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onDismissRequest) { Text(stringResource(R.string.ok)) }
            }
        }
    )
}

@Composable
fun launchPermissions(action: () -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        PermissionDeniedDialog {
            openDialog = false
        }
    }
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            action.invoke()
            Log.d("ExampleScreen", "PERMISSION GRANTED")
        } else {
            openDialog = true
            // Permission Denied: Do something
            Log.d("ExampleScreen", "PERMISSION DENIED")
        }
    }
}


fun checkForPermission(
    sdk_S_permission: String?,
    currentContext: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permission = when {
        sdk_S_permission != null -> sdk_S_permission
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Manifest.permission.ACCESS_FINE_LOCATION
        else -> Manifest.permission.ACCESS_COARSE_LOCATION
    }
    // Check permission
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            currentContext,
            permission
        ) -> {
            // Some works that require permission
            Log.d("ExampleScreen", "Code requires permission")
            launcher.launch(permission)
        }
        else -> {
            // Asking for permission
            launcher.launch(permission)
        }
    }
}


@Composable
fun checkMultiplePermissions(
    onSuccess: () -> Unit,
): ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> {
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        PermissionDeniedDialog {
            openDialog = false
        }
    }
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.all { permission -> permission.value }) onSuccess() else openDialog = true
    }
}