package com.techullurgy.oqrcodescanner.camera.presentation

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.techullurgy.oqrcodescanner.camera.utils.OQRCodeAnalyser
import com.techullurgy.oqrcodescanner.camera.utils.getCameraProvider
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }
    val preview: Preview = remember { Preview.Builder().build() }
    val imageAnalysis: ImageAnalysis = remember {
        ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val executor = remember { Executors.newSingleThreadExecutor() }
//    val imageAnalyser = remember { OImageAnalyser() }
    val imageAnalyser = remember { OQRCodeAnalyser() }

    LaunchedEffect(context, lifecycleOwner) {
        val cameraProvider: ProcessCameraProvider = context.getCameraProvider()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        imageAnalysis.setAnalyzer(executor, imageAnalyser)

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageAnalysis)
            .build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup)
    }

    LaunchedEffect(imageAnalyser) {
        imageAnalyser.imageAnalysisCompletionFlow.collectLatest {
            // Update UI
            Log.d("CameraX", it)
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier.fillMaxSize())
}