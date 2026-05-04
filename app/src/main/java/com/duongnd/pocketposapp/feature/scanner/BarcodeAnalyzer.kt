package com.duongnd.pocketposapp.feature.scanner

import android.content.Context
import android.graphics.Rect
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber

class BarcodeAnalyzer(
    context: Context,
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val appContext = context.applicationContext

    // ===== Scanner =====
    private val scanner by lazy {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13
            )
            .build()
        BarcodeScanning.getClient(options)
    }

    // ===== Scan Control =====
    private var lastBarcode: String? = null
    private var lastScanTime = 0L
    private val cooldown = 2500L

    private var isProcessing = false

    // ===== Feedback =====
    private val vibrator =
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val toneGenerator =
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true

        val rotation = imageProxy.imageInfo.rotationDegrees

        val image = InputImage.fromMediaImage(
            mediaImage,
            rotation
        )

        val cropRect = calculateCropRect(
            imageProxy.width,
            imageProxy.height
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                val barcode = barcodes.firstOrNull { candidate ->
                    val box = candidate.boundingBox ?: return@firstOrNull false
                    cropRect.contains(box)
                }

                val value = barcode?.rawValue ?: return@addOnSuccessListener

                handleBarcode(value)
            }
            .addOnFailureListener {
                Timber.e(it, "Barcode scan failed")
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }

    // ===== Handle barcode =====
    private fun handleBarcode(barcode: String) {

        val now = System.currentTimeMillis()

        if (barcode == lastBarcode && now - lastScanTime < cooldown) {
            return
        }

        lastBarcode = barcode
        lastScanTime = now

        Timber.d("Barcode detected: $barcode")

        playBeep()
        vibrate()

        onBarcodeDetected(barcode)
    }

    // ===== Calculate crop area (Khớp với Overlay) =====
    private fun calculateCropRect(
        width: Int,
        height: Int
    ): Rect {

        val widthRatio = 0.8f
        val heightRatio = 0.45f

        val cropWidth = (width * widthRatio).toInt()
        val cropHeight = (height * heightRatio).toInt()

        val left = (width - cropWidth) / 2
        val top = (height - cropHeight) / 2

        return Rect(
            left,
            top,
            left + cropWidth,
            top + cropHeight
        )
    }

    // ===== Beep =====
    private fun playBeep() {
        try {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
            Timber.e(e, "Beep error")
        }
    }

    // ===== Vibrate =====
    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        120,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(120)
            }
        } catch (e: Exception) {
            Timber.e(e, "Vibration error")
        }
    }

    fun release() {
        scanner.close()
        toneGenerator.release()
    }
}
