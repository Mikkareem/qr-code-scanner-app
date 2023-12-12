package com.techullurgy.oqrcodescanner.camera.utils

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val processCameraProvider: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
    processCameraProvider.addListener({
        val cameraProvider: ProcessCameraProvider = processCameraProvider.get()
        continuation.resume(cameraProvider)
    }, ContextCompat.getMainExecutor(this))
}