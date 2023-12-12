package com.techullurgy.oqrcodescanner.camera.utils

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

internal interface CameraImageAnalyzer<T> {
    val imageAnalysisCompletionFlow: Flow<T>
}