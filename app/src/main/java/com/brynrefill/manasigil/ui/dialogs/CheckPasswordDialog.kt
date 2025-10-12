package com.brynrefill.manasigil.ui.dialogs

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import com.brynrefill.manasigil.ui.components.ApiClient
import com.brynrefill.manasigil.ui.components.PasswordBreachResponse
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * dialog for checking if password has been breached.
 *
 * @param password - password to check
 * @param onDismiss - callback when close button is clicked
 */
@Composable
fun CheckPasswordDialog(
    password: String,
    onDismiss: () -> Unit
) {
    var isChecking by remember { mutableStateOf(false) }
    var breached by remember { mutableStateOf<Boolean?>(null) }
    var count by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // automatically check password when dialog opens
    LaunchedEffect(Unit) {
        isChecking = true
        ApiClient.solidalsApi.checkPassword(password)
            .enqueue(object : Callback<PasswordBreachResponse> {
                override fun onResponse(
                    call: Call<PasswordBreachResponse>,
                    response: Response<PasswordBreachResponse>
                ) {
                    isChecking = false
                    if (response.isSuccessful) {
                        breached = response.body()?.breached
                        count = response.body()?.count
                    } else {
                        errorMessage = "Error checking password!"
                    }
                }

                override fun onFailure(call: Call<PasswordBreachResponse>, t: Throwable) {
                    isChecking = false
                    errorMessage = "Network error: ${t.message}"
                }
            })
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // ??
                .background(Color(0xFF673AB7)) // set purple background
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // dialog title
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Check password",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // show loading indicator while checking
            if (isChecking) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 24.dp)
                )
                Text(
                    modifier = Modifier.padding(bottom = 24.dp),
                    text = "Checking password...",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White,
                )
            }

            // show results
            if (!isChecking && breached != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // ??
                        .background(Color(0xFF424242))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start // ??
                ) {
                    // breached status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // ??
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, // ??
                        verticalAlignment = Alignment.CenterVertically // ??
                    ) {
                        Text(
                            text = "Breached:",
                            fontSize = 18.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (breached == true) "YES" else "NO",
                            fontSize = 18.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (breached == true) Color(0xFFFF5252) else Color(0xFF4CAF50)
                        )
                    }

                    // count
                    Row(
                        modifier = Modifier.fillMaxWidth(), // ??
                        horizontalArrangement = Arrangement.SpaceBetween, // ??
                        verticalAlignment = Alignment.CenterVertically // ??
                    ) {
                        Text(
                            text = "Times found:",
                            fontSize = 18.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${count ?: 0}",
                            fontSize = 18.sp,
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // recommendation
                if (breached == true) {
                    Text(
                        text = "This password has been found in data breaches, so we strongly recommend you to refresh it!",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        color = Color(0xFFFF5252),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "This password has not been found in any known data breaches.",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                }
            }

            // show error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color(0xFFFF5252),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // CLOSE button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth() // ??
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "CLOSE",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
