package com.brynrefill.manasigil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * a single credential item.
 *
 * @param label - string that identify a credential item in the list, e.g. <service>.com
 * @param username
 * @param password
 * @param notes
 * @param isExpanded - if the credential item is expanded
 * @param isHighlighted - if the credential item is highlighted
 * @param onToggleExpand - callback function when credential item is expanded
 * @param onDelete - callback function when delete button is clicked
 */
@Composable
fun CredentialItem(
    label: String,
    username: String,
    password: String,
    notes: String,
    isExpanded: Boolean,
    isHighlighted: Boolean = false,
    onToggleExpand: () -> Unit,
    onDelete: () -> Unit = {}
) {
    // state to track if the item is expanded
    // var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()  // ?
    ) {
        // (always visible) togglable row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    // Color(0xFF424242)
                    if (isHighlighted) Color(0xFF4CAF50) // set green color
                    else Color(0xFF424242) // set default color
                )
                // .clickable { isExpanded = !isExpanded }, // toggle expansion on click
                .clickable { onToggleExpand() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = label,
                fontFamily = MontserratFontFamily,
                fontSize = 16.sp,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                // circle indicator as a stoplight.
                // Change color based on how long the password is not updated
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = Color.Green, // TODO: handle the live color changing logic
                            shape = CircleShape
                        )
                )
            }
        }
        // expanded section (shows details when isExpanded is true)
        if (isExpanded) {

            // expanded credential item details section
            Column(
                modifier = Modifier
                    .fillMaxWidth() // ??
                    // .height(240.dp) // 4x height expansion
                    .background(Color(0xFF616161)) // set lighter gray
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly // ?
            ) {
                // username text
                Text(
                    text = "Username: $username",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )

                // password text
                Text(
                    text = "Password: $password",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )

                // notes text
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Notes: $notes",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )

                // credential managing buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), // ??
                    // .padding(bottom = 32.dp),
                    // horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically // ??
                ) {
                    // EDIT button
                    Button(
                        onClick = {
                            // TODO: handle editing credential item logic
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit credential item",
                            tint = Color.White
                        )
                    }

                    // CHECK button
                    Button(
                        onClick = {
                            // TODO: handle checking if password is breached logic
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Check credential item",
                            tint = Color.White
                        )
                    }

                    // REFRESH button
                    Button(
                        onClick = {
                            // TODO: handle generation new strong password logic
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh credential item",
                            tint = Color.White
                        )
                    }

                    // DELETE button
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete credential item",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
