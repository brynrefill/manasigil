package com.brynrefill.manasigil.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.brynrefill.manasigil.data.model.CredentialData
import com.brynrefill.manasigil.utils.EncryptionHelper
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.SecretKey

/**
 * handles all Firestore operations for credentials.
 *
 * Responsibilities:
 * - load credentials from Firestore
 * - add new credentials
 * - update existing credentials
 * - delete credentials
 * - handle encryption/decryption
 */
class CredentialRepository(
    private val db: FirebaseFirestore,
    private val encryptionKey: SecretKey
) {

    // CRUD OPERATIONS

    /**
     * get all credential items from Firestore db for the current user
     */
    fun loadCredentials(
        userId: String,
        onSuccess: (List<CredentialData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // /users/{userId}/credentials/{credential}
        db.collection("users")
            .document(userId)
            .collection("credentials")
            .get()
            .addOnSuccessListener { documents ->
                val credentials = documents.mapNotNull { doc ->
                    Log.d(TAG, "${doc.id} => ${doc.data}")
                    try {
                        val encryptedPassword = doc.getString("password") ?: ""
                        val decryptedPassword = EncryptionHelper.decrypt(encryptedPassword, encryptionKey)

                        CredentialData(
                            label = doc.getString("label") ?: "",
                            username = doc.getString("username") ?: "",
                            password = decryptedPassword,
                            notes = doc.getString("notes") ?: "",
                            createdDate = doc.getLong("createdDate") ?: System.currentTimeMillis(),
                            documentId = doc.id
                        )
                    } catch (exception: Exception) {
                        Log.w(TAG, "Error loading corrupted entries.", exception)
                        null // skip corrupted entries
                    }
                }
                onSuccess(credentials)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                onFailure(exception)
            }
    }

    /**
     * add a new credential item to Firestore db
     */
    fun addCredential(
        userId: String,
        credential: CredentialData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // encrypt password before storing
        val encryptedPassword = EncryptionHelper.encrypt(credential.password, encryptionKey)

        val credentialData = hashMapOf(
            "label" to credential.label,
            "username" to credential.username,
            "password" to encryptedPassword,
            "notes" to credential.notes,
            "createdDate" to credential.createdDate
        )

        // add a new document with a generated ID
        // /users/{userId}/credentials/{credential}
        db.collection("users")
            .document(userId)
            .collection("credentials")
            .add(credentialData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
                onFailure(exception)
            }
    }

    /**
     * update an existing credential item in Firestore db
     */
    fun updateCredential(
        userId: String,
        credential: CredentialData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // encrypt password before storing
        val encryptedPassword = EncryptionHelper.encrypt(credential.password, encryptionKey)

        val credentialData = hashMapOf(
            "label" to credential.label,
            "username" to credential.username,
            "password" to encryptedPassword,
            "notes" to credential.notes,
            "createdDate" to credential.createdDate
        )

        // /users/{userId}/credentials/{credential}
        db.collection("users")
            .document(userId)
            .collection("credentials")
            .document(credential.documentId)
            .set(credentialData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    /**
     * delete a credential item from Firestore db
     */
    fun deleteCredential(
        userId: String,
        documentId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // /users/{userId}/credentials/{credential}
        db.collection("users")
            .document(userId)
            .collection("credentials")
            .document(documentId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}