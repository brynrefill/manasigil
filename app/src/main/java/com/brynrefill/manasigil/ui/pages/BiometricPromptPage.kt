package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * biometric dialog to ask for authentication.
 *
 * @param onUnlockClick - callback when biometric sensor used to unlock the page
 */
@Composable
fun BiometricPromptPage(
    onUnlockClick: () -> Unit
) {
    // show biometric prompt page
    // TODO: refactor it creating BiometricAuthDialog()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)), // set purple background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Authenticate to continue!",
                fontSize = 20.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Button(
                onClick = { onUnlockClick },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434) // set gray color
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "UNLOCK",
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )
            }
        }
    }
}
