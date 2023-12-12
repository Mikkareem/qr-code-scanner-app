package com.techullurgy.oqrcodescanner.camera.utils

import android.graphics.Bitmap
import android.graphics.PixelFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.nio.ByteBuffer

class OImageAnalyser: ImageAnalysis.Analyzer, CameraImageAnalyzer<Bitmap> {

    private val supportedFormats = listOf(
        PixelFormat.RGBA_8888
    )

    private var mImageAnalysisCompletionListener: ImageAnalysisCompletionListener<Bitmap>? = null

    override fun analyze(image: ImageProxy) {
        if(image.format in supportedFormats) {
            image.use {
                val bitmap = it.toBitmap()
                // Process Bitmap and notify callback
                mImageAnalysisCompletionListener?.analysed(value = bitmap)
            }
        }
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val byteBuffer: ByteBuffer = planes[0].buffer
        byteBuffer.rewind()
        bitmap.copyPixelsFromBuffer(byteBuffer)
        return bitmap
    }

    private interface ImageAnalysisCompletionListener<T> {
        fun analysed(value: T)
    }

    override val imageAnalysisCompletionFlow: Flow<Bitmap>
        get() = callbackFlow {
            val imageAnalysisCompletionListener: ImageAnalysisCompletionListener<Bitmap> =
                object : ImageAnalysisCompletionListener<Bitmap> {
                    override fun analysed(value: Bitmap) {
                        trySend(value)
                    }
                }

            mImageAnalysisCompletionListener = imageAnalysisCompletionListener

            awaitClose {
                mImageAnalysisCompletionListener = null
            }
        }
}