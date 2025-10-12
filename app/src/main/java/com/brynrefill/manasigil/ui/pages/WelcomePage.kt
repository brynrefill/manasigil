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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.components.CredentialData
import com.brynrefill.manasigil.ui.components.CredentialItem
import com.brynrefill.manasigil.ui.dialogs.AddCredentialDialog
import com.brynrefill.manasigil.ui.dialogs.CheckPasswordDialog
import com.brynrefill.manasigil.ui.dialogs.DeleteConfirmationDialog
import com.brynrefill.manasigil.ui.dialogs.LogoutConfirmationDialog
import com.brynrefill.manasigil.ui.dialogs.RefreshPasswordDialog
import com.brynrefill.manasigil.ui.dialogs.SearchDialog
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * welcome page shown after successful account creation or sign in.
 *
 * @param username - the username of the registered/logged-in user
 * @param isNew - state if the user is a new user
 * @param onLogout - callback function when logout button is clicked
 * @param onHelpClick - callback function when help button is clicked
 * @param onSettingsClick - callback function when settings button is clicked
 * @param onLoadCredentials - callback function to load credential items list from db
 * @param onAddCredential - callback function to add credential item to db
 * @param onUpdateCredential - callback function to update credential item in db
 * @param onDeleteCredential - callback function to delete credential item from db
 */
@Composable
fun WelcomePage(
    username: String,
    isNew: Boolean,
    onLogout: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoadCredentials: ((List<CredentialData>) -> Unit) -> Unit = {},
    // onAddCredential: (CredentialData, () -> Unit, (Exception) -> Unit) -> Unit = { _, _, _ -> },
    onAddCredential: (CredentialData, () -> Unit) -> Unit = { _, _ -> },
    // onUpdateCredential: (CredentialData, () -> Unit, (Exception) -> Unit) -> Unit = { _, _, _ -> },
    onUpdateCredential: (CredentialData, () -> Unit) -> Unit = { _, _ -> },
    // onDeleteCredential: (String, () -> Unit, (Exception) -> Unit) -> Unit = { _, _, _ -> },
    onDeleteCredential: (String, () -> Unit) -> Unit = { _, _ -> }
) {
    // remember the scroll state of the credentials list, when content overflows
    val scrollState = rememberScrollState()

    // state to control the add credential item dialog visibility
    var showAddDialog by remember { mutableStateOf(false) }

    // state to control the logout dialog visibility
    var showLogoutDialog by remember { mutableStateOf(false) }

    // state to track expanded item index, because only one item can be expanded at a time
    var expandedItemIndex by remember { mutableStateOf<Int?>(null) }

    // state to track credential item to delete
    var itemToDelete by remember { mutableStateOf<Int?>(null) }

    // add states to track searched credential item
    var showSearchDialog by remember { mutableStateOf(false) }

    // var highlightedItemIndex by remember { mutableStateOf<Int?>(null) }
    var highlightedItemIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // state to track which item is being edited
    var itemToEdit by remember { mutableStateOf<Int?>(null) }

    // state to track credentials list
    var credentialsList by remember { mutableStateOf<List<CredentialData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var itemToRefresh by remember { mutableStateOf<Int?>(null) }
    var itemToCheck by remember { mutableStateOf<Int?>(null) }

    // load credentials when welcome page appears
    LaunchedEffect(Unit) {
        onLoadCredentials { credentials ->
            credentialsList = credentials
            isLoading = false
        }
    }

    // state to store the list of credentials
    /*
    var credentialsList by remember { mutableStateOf(listOf(
        CredentialData("<credential1>", "<username1>", "<password1>", "<notes1>", System.currentTimeMillis()),
        CredentialData("<credential2>", "<username2>", "<password2>", "<notes2>", System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)), // 6 months ago
        CredentialData("<credential3>", "<username3>", "<password3>", "<notes3>", System.currentTimeMillis() - (150L * 24 * 60 * 60 * 1000))  // 5 months ago
        // the L means long literal, ensuring the computation uses long type to avoid integer overflow
    )) }
    */

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

            // show loading indicator while fetching credentials
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), // ?
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {

                val welcomeMessage =
                    if (isNew) "Welcome!" else "Good to see you!" // or "Let's get started!"

                Text(
                    modifier = Modifier.padding(bottom = 32.dp),
                    text = "$welcomeMessage\n$username",
                    fontSize = 28.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // "control" buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    ),
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
                        onClick = { showSearchDialog = true },
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
                        onClick = onSettingsClick,
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
                        onClick = onHelpClick,
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
                        createdDate = credential.createdDate,
                        isExpanded = expandedItemIndex == index,
                        // isHighlighted = highlightedItemIndex == index,
                        isHighlighted = highlightedItemIndices.contains(index),
                        onToggleExpand = {
                            /*
                            // if highlighted, clear highlight when item is clicked/expanded
                            if (highlightedItemIndex == index) {
                                highlightedItemIndex = null
                            }
                            */
                            // clear highlight for all items when an item is clicked/expanded
                            if (highlightedItemIndices.contains(index)) {
                                // highlightedItemIndices = highlightedItemIndices - index
                                highlightedItemIndices = emptySet()
                            }
                            // toggle expansion
                            expandedItemIndex = if (expandedItemIndex == index) null else index
                        },
                        onEdit = {
                            itemToEdit = index
                        },
                        onRefresh = {
                            itemToRefresh = index
                        },
                        onCheck = {
                            itemToCheck = index
                        },
                        onDelete = {
                            /*
                            // delete the credential item at this index

                            credentialsList = credentialsList.filterIndexed { i, _ -> i != index }

                            // reset expanded state if the deleted item was expanded
                            if (expandedItemIndex == index) {
                                expandedItemIndex = null
                            }
                            */
                            // show confirmation dialog instead of deleting immediately the item
                            itemToDelete = index
                        }
                    )

                    if (index < credentialsList.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // add credential dialog
        /*if (showAddDialog) {
            AddCredentialDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { label, username, password, notes ->
                    // add new credential to the list
                    credentialsList = credentialsList + CredentialData(label, username, password, notes, System.currentTimeMillis())
                    showAddDialog = false
                }
            )
        }*/
        if (showAddDialog) {
            AddCredentialDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { label, username, password, notes ->
                    val newCredential = CredentialData(label, username, password, notes, System.currentTimeMillis())
                    onAddCredential(
                        newCredential,
                        {
                            // success - reload credentials
                            onLoadCredentials { credentials ->
                                credentialsList = credentials
                            }
                            showAddDialog = false
                            /*
                            Toast.makeText(
                                // Note: We need context here, will fix in MainActivity
                                null,
                                "Credential added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            */
                        }
                        /*,
                        { exception ->
                        }
                        */
                    )
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

        // edit credential dialog
        /*if (itemToEdit != null) {
            val credentialToEdit = credentialsList[itemToEdit!!]
            AddCredentialDialog(
                onDismiss = { itemToEdit = null },
                onConfirm = { label, username, password, notes ->
                    // replace the credential at this index with updated data
                    credentialsList = credentialsList.toMutableList().also {
                        it[itemToEdit!!] = CredentialData(label, username, password, notes, credentialToEdit.createdDate)
                    }
                    itemToEdit = null
                },
                initialData = credentialToEdit,
                isEditMode = true
            )
        }*/
        if (itemToEdit != null) {
            val credentialToEdit = credentialsList[itemToEdit!!]
            AddCredentialDialog(
                onDismiss = { itemToEdit = null },
                onConfirm = { label, username, password, notes ->
                    val updatedCredential = CredentialData(
                        label, username, password, notes,
                        credentialToEdit.createdDate,
                        credentialToEdit.documentId
                    )
                    onUpdateCredential(
                        updatedCredential,
                        {
                            // success - reload credentials
                            onLoadCredentials { credentials ->
                                credentialsList = credentials
                            }
                            itemToEdit = null
                        }
                        /*,
                        { exception ->
                        }
                        */
                    )
                },
                initialData = credentialToEdit,
                isEditMode = true
            )
        }

        // delete confirmation dialog
        /*if (itemToDelete != null) {
            DeleteConfirmationDialog(
                label = credentialsList[itemToDelete!!].label,
                onDismiss = { itemToDelete = null },
                onConfirm = {
                    // delete the credential at this index
                    credentialsList = credentialsList.filterIndexed { i, _ -> i != itemToDelete }
                    // reset expanded state if the deleted item was expanded
                    if (expandedItemIndex == itemToDelete) {
                        expandedItemIndex = null
                    }
                    itemToDelete = null
                }
            )
        }*/
        if (itemToDelete != null) {
            DeleteConfirmationDialog(
                label = credentialsList[itemToDelete!!].label,
                onDismiss = { itemToDelete = null },
                onConfirm = {
                    val credential = credentialsList[itemToDelete!!]
                    onDeleteCredential(
                        credential.documentId,
                        {
                            // success - reload credentials
                            onLoadCredentials { credentials ->
                                credentialsList = credentials
                            }
                            if (expandedItemIndex == itemToDelete) {
                                expandedItemIndex = null
                            }
                            itemToDelete = null
                        }
                        /*,
                        { exception ->
                        }
                        */
                    )
                }
            )
        }

        // refresh password dialog
        if (itemToRefresh != null) {
            val credential = credentialsList[itemToRefresh!!]
            RefreshPasswordDialog(
                onDismiss = { itemToRefresh = null },
                onConfirm = { newPassword ->
                    // update credential item with new password
                    val updatedCredential = credential.copy(password = newPassword)
                    onUpdateCredential(
                        updatedCredential,
                        {
                            // success - reload credentials
                            onLoadCredentials { credentials ->
                                credentialsList = credentials
                            }
                            itemToRefresh = null
                        }
                    )
                }
            )
        }

        // check password breach dialog
        if (itemToCheck != null) {
            val credential = credentialsList[itemToCheck!!]
            CheckPasswordDialog(
                password = credential.password,
                onDismiss = { itemToCheck = null }
            )
        }

        // search dialog
        if (showSearchDialog) {
            SearchDialog(
                onDismiss = { showSearchDialog = false },
                onSearch = { searchText ->
                    /*
                    // find credential that contains the search text (case insensitive)
                    val foundIndex = credentialsList.indexOfFirst { credential ->
                        credential.label.contains(searchText, ignoreCase = true)
                    }
                    if (foundIndex != -1) {
                        highlightedItemIndex = foundIndex
                    }
                    */
                    // find all credentials that contain the search text (case insensitive)
                    val foundIndices = credentialsList.mapIndexedNotNull { index, credential ->
                        if (credential.label.contains(searchText, ignoreCase = true)) {
                            index
                        } else {
                            null
                        }
                    }.toSet()

                    highlightedItemIndices = foundIndices
                    showSearchDialog = false
                }
            )
        }
    }
}
