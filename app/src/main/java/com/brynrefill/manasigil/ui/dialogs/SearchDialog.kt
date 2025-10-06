package com.brynrefill.manasigil.ui.dialogs

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * dialog for searching credentials.
 *
 * @param onDismiss - callback when dialog is dismissed
 * @param onSearch - callback when go button is clicked
 */
@Composable
fun SearchDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

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
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Search credential",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // content
            Row(
                modifier = Modifier.fillMaxWidth(), // ?
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically // ?
            ) {
                // search text field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            text = "NAME",
                            fontFamily = MontserratFontFamily,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF424242),
                        unfocusedContainerColor = Color(0xFF424242),
                        focusedBorderColor = Color(0xFF424242),
                        unfocusedBorderColor = Color(0xFF424242)
                    ),
                    shape = RoundedCornerShape(0.dp), // ?
                    singleLine = true
                )

                // GO button
                Button(
                    onClick = {
                        if (searchText.isNotEmpty()) {
                            onSearch(searchText)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        text = "GO",
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
