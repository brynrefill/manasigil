package com.brynrefill.manasigil.ui.pages

import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * camera view for scanning QR codes.
 *
 * @param onQRCodeScanned - callback when QR code is detected with the raw value
 * @param onDismiss - callback when close button is clicked
 * @param onManualEntry - callback when enter credential manually button is clicked
 */
@Composable
fun QRScannerPage(
    onQRCodeScanned: (String) -> Unit,
    onDismiss: () -> Unit,
    onManualEntry: () -> Unit
) {
    var hasScanned by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize() // ??
            .background(Color.Black)
    ) {
        // camera preview
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // preview
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // image analysis for QR detection
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->
                        processImageProxy(imageProxy, hasScanned) { qrCode ->
                            if (!hasScanned) {
                                hasScanned = true
                                onQRCodeScanned(qrCode)
                            }
                        }
                    }

                    // camera selector
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            context as ComponentActivity,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))

                previewView
            },
            modifier = Modifier.fillMaxSize() // ??
        )

        // overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize() // ??
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween // ??
        ) {
            // CLOSE button
            Row(
                modifier = Modifier.fillMaxWidth(), // ??
                horizontalArrangement = Arrangement.SpaceBetween // ??
             ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // scanning frame
            Box(
                modifier = Modifier
                    .fillMaxWidth() // ??
                    .weight(1f),
                contentAlignment = Alignment.Center // ??
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally // ??
                ) {
                    // scanning frame
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .border(3.dp, Color.White, RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Position QR code within the frame",
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ENTER CREDENTIAL MANUALLY button
            Button(
                onClick = onManualEntry,
                modifier = Modifier
                    .fillMaxWidth() // ??
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "Enter credential manually",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * process camera image to detect QR codes
 */
@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    hasScanned: Boolean,
    onQRCodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null && !hasScanned) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_TEXT,
                        Barcode.TYPE_URL -> {
                            barcode.rawValue?.let { value ->
                                onQRCodeDetected(value)
                            }
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
