package com.brynrefill.manasigil.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.brynrefill.manasigil.data.api.ApiClient
import com.brynrefill.manasigil.data.api.PasswordGeneratorResponse
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * dialog to generate a new password via external API.
 *
 * @param onDismiss - callback function when cancel button is clicked
 * @param onConfirm - callback function when take button is clicked
 */
@Composable
fun RefreshPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var generatedPassword by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val passwordLength = 16 // TODO: make this configurable by the user

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
                text = "Generate password",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // password text field and gen button
            Row(
                modifier = Modifier
                    .fillMaxWidth() // ?
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically // ?
            ) {
                // password text field
                OutlinedTextField(
                    value = generatedPassword,
                    onValueChange = {
                        generatedPassword = it
                        errorMessage = "" // clear error when user types
                    },
                    placeholder = {
                        Text(
                            text = "PASSWORD",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF424242),
                        unfocusedContainerColor = Color(0xFF424242),
                        focusedBorderColor = Color(0xFF424242),
                        unfocusedBorderColor = Color(0xFF424242)
                    ),
                    shape = RoundedCornerShape(0.dp), // ?
                    singleLine = true,
                    readOnly = true
                )

                // GEN button
                Button(
                    onClick = {
                        isGenerating = true
                        errorMessage = ""

                        // call the API
                        ApiClient.solidalsApi.generatePassword(passwordLength)
                            .enqueue(object : Callback<PasswordGeneratorResponse> {
                                override fun onResponse(
                                    call: Call<PasswordGeneratorResponse>,
                                    response: Response<PasswordGeneratorResponse>
                                ) {
                                    isGenerating = false
                                    if (response.isSuccessful) {
                                        generatedPassword = response.body()?.password ?: ""
                                    } else {
                                        // handle error
                                        errorMessage = "Error generating password!"
                                    }
                                }

                                override fun onFailure(call: Call<PasswordGeneratorResponse>, t: Throwable) {
                                    isGenerating = false
                                    errorMessage = "Network error: ${t.message}"
                                }
                            })
                    },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F0977)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF424242)), // set button border
                    shape = RoundedCornerShape(0.dp),
                    enabled = !isGenerating
                ) {
                    // show loading indicator int the gen button while generating
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "GEN",
                            fontSize = 14.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            // error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color(0xFFFF5252), // set red color
                    modifier = Modifier
                        .fillMaxWidth() // ??
                        .padding(bottom = 16.dp)
                )
            }

            // cancel and take buttons
            Row(
                modifier = Modifier.fillMaxWidth(), // ?
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CANCEL button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
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

                // TAKE button
                Button(
                    onClick = {
                        if (generatedPassword.isEmpty()) {
                            // don't accept if there's no password
                            errorMessage = "Generate a password first!"
                        } else if (generatedPassword.startsWith("Error") ||
                            generatedPassword.startsWith("Network")) {
                            // don't accept if there's an error
                            errorMessage = "Please generate a valid password!"
                        } else {
                            onConfirm(generatedPassword)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "TAKE",
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
