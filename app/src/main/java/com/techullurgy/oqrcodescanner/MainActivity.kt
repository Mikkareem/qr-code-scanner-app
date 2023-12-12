package com.techullurgy.oqrcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.techullurgy.oqrcodescanner.camera.presentation.CameraScreen
import com.techullurgy.oqrcodescanner.camera.presentation.rememberCameraPermission
import com.techullurgy.oqrcodescanner.ui.theme.OQRCodeScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OQRCodeScannerTheme {
                val cameraPermissionGranted = rememberCameraPermission()

                if(cameraPermissionGranted) {
                    CameraScreen(
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
        }
    }
}
