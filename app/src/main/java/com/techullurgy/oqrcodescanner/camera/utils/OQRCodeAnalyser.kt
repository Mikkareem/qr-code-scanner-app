package com.techullurgy.oqrcodescanner.camera.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OQRCodeAnalyser: ImageAnalysis.Analyzer, CameraImageAnalyzer<String> {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        )
        .build()

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    override fun analyze(image: ImageProxy) {
        QRCodeScanner().scanQRCode(image)
    }

    inner class QRCodeScanner {
        @OptIn(ExperimentalGetImage::class)
        fun scanQRCode(imageProxy: ImageProxy) {
            val inputImage = InputImage.fromMediaImage(
                imageProxy.image!!, imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        val rawValue = barcode.rawValue
                        val valueType = barcode.valueType
                        mImageAnalysisCompletionListener?.analysed("$rawValue ------ $valueType")
                    }
                }
                .addOnFailureListener {

                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private var mImageAnalysisCompletionListener: ImageAnalysisCompletionListener<String>? = null

    override val imageAnalysisCompletionFlow: Flow<String>
        get() = callbackFlow {
            val imageAnalysisCompletionListener = object : ImageAnalysisCompletionListener<String> {
                override fun analysed(value: String) {
                    trySend(value)
                }
            }
            mImageAnalysisCompletionListener = imageAnalysisCompletionListener
            awaitClose {
                mImageAnalysisCompletionListener = null
            }
        }

    interface ImageAnalysisCompletionListener<T> {
        fun analysed(value: T)
    }
}