package com.brynrefill.manasigil.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.brynrefill.manasigil.data.model.CredentialData
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * a dialog for adding a new credential in the credentials list.
 *
 * @param onDismiss - callback function when cancel button is clicked
 * @param onConfirm - callback function when confirm button is clicked with (label, username, password, notes)
 * @param initialData - credential item data to be modified
 * @param isEditMode - if the item is being edited
 * @param onAutomaticEntry - callback when import from qr code button is clicked
 */
@Composable
fun AddCredentialDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit,
    initialData: CredentialData? = null,
    isEditMode: Boolean = false,
    onAutomaticEntry: () -> Unit
) {
    // state variables for the text fields
    var label by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // state to track password visibility
    var isPasswordVisible by remember { mutableStateOf(false) }

    // to properly initialize text fields with existing data
    // when initialData is provided
    LaunchedEffect(initialData) {
        if (initialData != null) {
            label = initialData.label
            username = initialData.username
            password = initialData.password
            notes = initialData.notes
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        // dialog content
        Column(
            modifier = Modifier
                .fillMaxWidth() // ?
                .background(Color(0xFF673AB7)) // set purple background
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // dialog title
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                // text = "Add credential",
                text = if (isEditMode) "Edit credential" else "Add credential",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // item title field
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                placeholder = {
                    Text(
                        text = "ITEM NAME",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth() // ?
                    .padding(bottom = 16.dp),
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

            // username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = {
                    Text(
                        text = "EMAIL/USERNAME",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth() // ?
                    .padding(bottom = 16.dp),
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

            // password field and toggle visibility button
            Row(
                modifier = Modifier.fillMaxWidth(), // ?
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "PASSWORD",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    visualTransformation = if (!isPasswordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    modifier = Modifier
                        // .fillMaxWidth() // ?
                        .weight(6f)
                        .padding(bottom = 16.dp),
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

                // TOGGLE VISIBILITY PASSWORD button
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier
                        // .size(24.dp)
                        .weight(1f),
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White,
                        // modifier = Modifier.size(20.dp)
                    )
                }
            }

            // notes field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = {
                    Text(
                        text = "NOTES",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth() // ?
                    .padding(bottom = 16.dp)
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF424242),
                    unfocusedContainerColor = Color(0xFF424242),
                    focusedBorderColor = Color(0xFF424242),
                    unfocusedBorderColor = Color(0xFF424242)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                maxLines = 5
            )

            // IMPORT FROM QR CODE button
            Button(
                onClick = onAutomaticEntry,
                modifier = Modifier
                    .fillMaxWidth() // ??
                    .padding(bottom = 24.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "Import from QR code",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // add credential dialog buttons
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
                        containerColor = Color(0xFF373434) // set light gray background
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

                // OK button
                Button(
                    onClick = {
                        if (label.isNotEmpty()) {
                            onConfirm(label.trim(), username.trim(), password.trim(), notes.trim())
                        }
                    },
                    modifier = Modifier
                        // .fillMaxWidth()
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "OK",
                        fontSize = 16.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
