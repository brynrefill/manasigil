package com.brynrefill.manasigil.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.pages.QRScannerPage

@Preview(showBackground = true)
@Composable
fun QRScannerPagePreview() {
    // since it is a preview it does nothing
    QRScannerPage(onQRCodeScanned = {}, onDismiss = {}, onManualEntry = {})
}
