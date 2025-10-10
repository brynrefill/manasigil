package com.brynrefill.manasigil.ui.components

/**
 * data class to represent a credential item in credentials list.
 *
 * @param label - title of a credential item
 * @param username - username for this item
 * @param password - password for this item
 * @param notes - additional notes about this item
 * @param createdDate - date when the credential item is created
 * @param documentId - credential item id
 */
data class CredentialData(
    val label: String,
    val username: String,
    val password: String,
    val notes: String,
    val createdDate: Long = System.currentTimeMillis(), // timestamp in milliseconds
    val documentId: String = "" // Firestore document ID
)
