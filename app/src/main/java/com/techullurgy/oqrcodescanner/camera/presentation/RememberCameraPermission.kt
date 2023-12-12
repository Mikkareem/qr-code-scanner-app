package com.techullurgy.oqrcodescanner.camera.presentation

import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
internal fun rememberCameraPermission(): Boolean {
    val context = LocalContext.current

    var isCameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    if(!isCameraPermissionGranted) {
        val permissionRequestLauncher = rememberRequestPermissionLauncherForActivityResult(
            onPermissionNotGranted = { isCameraPermissionGranted = false },
            onPermissionGranted = { isCameraPermissionGranted = true }
        )
        SideEffect {
            // This can't be called in composition scope
            permissionRequestLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    return isCameraPermissionGranted
}

@Composable
private fun rememberRequestPermissionLauncherForActivityResult(
    onPermissionNotGranted: () -> Unit,
    onPermissionGranted: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(isGranted) onPermissionGranted() else onPermissionNotGranted()
    }
}