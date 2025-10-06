package com.brynrefill.manasigil.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.window.Dialog
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * a dialog to confirm delete action.
 *
 * @param label - string that identify the credential item
 * @param onDismiss - callback function when cancel button is clicked
 * @param onConfirm - callback function when confirm button is clicked
 */
@Composable
fun DeleteConfirmationDialog(
    label: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // ?
                .background(Color(0xFF673AB7)) // set purple background
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // dialog title
            Text(
                // modifier = Modifier.padding(bottom = 16.dp),
                text = "You are about to delete $label item...",
                fontSize = 16.sp, // 24.dp
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // dialog message
            Text(
                modifier = Modifier.padding(bottom = 24.dp), // 32.dp
                text = "are you sure?",
                fontSize = 24.sp, // 16.dp
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // logout dialog buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CANCEL button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434) // set gray background
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "CANCEL",
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // CONFIRM button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF673AB7) // set purple background
                    ),
                    border = BorderStroke(2.dp, Color(0xFF373434)), // set button border
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
