package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import kotlin.collections.plus
import com.brynrefill.manasigil.ui.components.CredentialData
import com.brynrefill.manasigil.ui.components.CredentialItem
import com.brynrefill.manasigil.ui.dialogs.AddCredentialDialog
import com.brynrefill.manasigil.ui.dialogs.LogoutConfirmationDialog
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * welcome page shown after successful account creation or sign in.
 *
 * @param username - the username of the registered/logged-in user
 * @param isNew - state if the user is a new user
 * @param onLogout - callback function when logout button is clicked
 */
@Composable
fun WelcomePage(
    username: String,
    isNew: Boolean,
    onLogout: () -> Unit = {}
) {
    // remember the scroll state of the credentials list, when content overflows
    val scrollState = rememberScrollState()

    // state to control the add credential item dialog visibility
    var showAddDialog by remember { mutableStateOf(false) }

    // state to control the logout dialog visibility
    var showLogoutDialog by remember { mutableStateOf(false) }

    // state to track expanded item index, because only one item can be expanded at a time
    var expandedItemIndex by remember { mutableStateOf<Int?>(null) }

    // state to store the list of credentials
    var credentialsList by remember { mutableStateOf(listOf(
        CredentialData("<credential1>", "<username1>", "<password1>", "<notes1>"),
        CredentialData("<credential2>", "<username2>", "<password2>", "<notes2>"),
        CredentialData("<credential3>", "<username3>", "<password3>", "<notes3>")
    )) }

    Box(
        modifier = Modifier
            .fillMaxSize() // ?
            .background(Color(0xFF673AB7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // ?
                .verticalScroll(scrollState) // enable vertical scrolling
                .padding(32.dp),
            horizontalAlignment = Alignment.Start // ?
        ) {
            // add space at top
            Spacer(modifier = Modifier.height(24.dp))

            val welcomeMessage = if (isNew) "Welcome!" else "Good to see you!" // or "Let's get started!"

            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "$welcomeMessage\n$username",
                fontSize = 28.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // row with "control" buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically // ?
            ) {
                // ADD button
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(50.dp), // 40.dp
                    contentPadding = PaddingValues(0.dp)
                ) {
                    /*
                    Text(
                        text = "ADD",
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    */
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add credential",
                        tint = Color.White
                    )
                }

                // SEARCH button
                Button(
                    onClick = {
                        // TODO: handle search a credential in the credentials list logic
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search credential",
                        tint = Color.White
                    )
                }

                // SETTINGS button
                Button(
                    onClick = {
                        // TODO: handle call to settings page logic
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings page",
                        tint = Color.White
                    )
                }

                // HELP button
                Button(
                    onClick = {
                        // TODO: handle call to help page logic
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Help page",
                        tint = Color.White
                    )
                }

                // LOGOUT button
                Button(
                    onClick = { showLogoutDialog = true }, // onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF373434)
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }

            // list of all the saved credentials
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Credentials list",
                fontSize = 20.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            /*
            CredentialItem(label = "<credential1>")
            Spacer(modifier = Modifier.height(16.dp))
            CredentialItem(label = "<credential2>")
            Spacer(modifier = Modifier.height(16.dp))
            CredentialItem(label = "<credential3>")
            */

            // list of credentials from state
            credentialsList.forEachIndexed { index, credential ->
                CredentialItem(
                    label = credential.label,
                    username = credential.username,
                    password = credential.password,
                    notes = credential.notes,
                    isExpanded = expandedItemIndex == index,
                    onToggleExpand = {
                        expandedItemIndex = if (expandedItemIndex == index) null else index
                    }
                )

                if (index < credentialsList.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // add credential dialog
        if (showAddDialog) {
            AddCredentialDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { label, username, password, notes ->
                    // add new credential to the list
                    credentialsList = credentialsList + CredentialData(label, username, password, notes)
                    showAddDialog = false
                }
            )
        }

        // logout confirmation dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    showLogoutDialog = false
                    onLogout()
                }
            )
        }
    }
}
