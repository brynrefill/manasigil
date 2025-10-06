package com.brynrefill.manasigil.ui.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.dialogs.DeleteConfirmationDialog

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview() {
    // wrap in a Box with purple background to simulate the app context
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)),
        contentAlignment = Alignment.Center
    ) {
        // since it is a preview it does nothing
        DeleteConfirmationDialog("Google.com", onDismiss = {}, onConfirm = {})
    }
}
