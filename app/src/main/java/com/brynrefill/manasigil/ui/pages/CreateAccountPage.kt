package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * create account page with account creation form.
 *
 * @param onBackClick - callback function when back button is clicked
 * @param onCreateAccount - callback function when submit button is clicked
 */
@Composable
fun CreateAccountPage(
    onBackClick: () -> Unit = {},
    onCreateAccount: (String, String, String) -> Unit = { _, _, _ -> }
) {
    // state variables to hold the text field values
    // remember and mutableStateOf allows the UI to update when values change
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    // Box allows to overlay the back button on top of the content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)) // set purple background
    ) {
        // back button in top left corner
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
            // .padding(16.dp)
        ) {
            /*Text(
                modifier = Modifier.padding(top = 40.dp), // add space above back button
                text = "X",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )*/
            Icon(
                modifier = Modifier.padding(top = 60.dp), // 40.dp // add space above back button
                imageVector = Icons.Filled.Close,
                contentDescription = "Close page",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                // .background(Color(0xFF673AB7)) // set purple background
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // app name (logo)
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Manasigil",
                fontSize = 36.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // call to action text
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "Create your Manasigil account.",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )

            // e-mail text field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it }, // update email state when text changes
                placeholder = {
                    Text(
                        text = "E-MAIL",
                        color = Color.White.copy(alpha = 0.6f) // set semi-transparent white color
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // ?
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434), // set dark gray background
                    unfocusedContainerColor = Color(0xFF373434), // set dark gray background
                    focusedBorderColor = Color(0xFF373434), // set dark gray border
                    unfocusedBorderColor = Color(0xFF373434) // set dark gray border
                ),
                shape = RoundedCornerShape(0.dp), // ? // sharp corners
                singleLine = true // keep text on one line
            )

            // password text field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it }, // update password state when text changes
                placeholder = {
                    Text(
                        text = "PASSWORD",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(), // hide password with dots
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434),
                    unfocusedContainerColor = Color(0xFF373434),
                    focusedBorderColor = Color(0xFF373434),
                    unfocusedBorderColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                singleLine = true
            )

            // repeat password text field
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it }, // update repeatPassword state when text changes
                placeholder = {
                    Text(
                        text = "REPEAT PASSWORD",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434),
                    unfocusedContainerColor = Color(0xFF373434),
                    focusedBorderColor = Color(0xFF373434),
                    unfocusedBorderColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                singleLine = true
            )

            // submit button
            Button(
                onClick = {
                    // call the callback with the form data
                    onCreateAccount(email, password, repeatPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF424242) // set light gray background
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // bottom section with footer
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above footer
                text = "© 2025 brynrefill.com",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }
    }
}
