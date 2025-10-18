package com.brynrefill.manasigil.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * uses AES-256-GCM encryption to secure passwords before storing credential items in Firestore
 */
object EncryptionHelper {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "ManasigilEncryptionKey"

    /**
     * get or create (one-time setup) an AES encryption key from Android Keystore.
     * The key is stored securely and persists across app sessions
     */
    fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        // check if key already exists
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }

        // generate new key and store in Keystore
        val keyGenerator = KeyGenerator.getInstance(
            // "AES"
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            // the device must have secure lock screen enabled (PIN, pattern, or password).
            // Cannot enforce just a PIN; Android uses whatever the user has configured
            .setUserAuthenticationRequired(false) // set to true to have biometric/PIN protection
            .build()

        // keyGenerator.init(KEY_SIZE, SecureRandom())
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     * delete the encryption key from Keystore.
     * This will make all encrypted data unrecoverable, so PAY ATTENTION!
     * Seems useless, but added for completion
     */
    fun deleteKey() {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        keyStore.deleteEntry(KEY_ALIAS)
    }

    /**
     * encrypt a password using AES-GCM.
     * Returns Base64 encoded string with format: IV:EncryptedData
     */
    fun encrypt(plaintext: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)

        //  when using Android Keystore, the system generates the IV automatically
        //  and doesn't allow you to provide your own. Need to modify the encryption to
        //  let the Cipher generate the IV.
        // val iv = ByteArray(GCM_IV_LENGTH)
        // SecureRandom().nextBytes(iv)
        // val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        // cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        // Let the cipher generate the IV internally
        cipher.init(Cipher.ENCRYPT_MODE, key)
        // get the generated IV
        val iv = cipher.iv

        val encryptedData = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // combine IV and encrypted data
        val combined = iv + encryptedData
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * decrypt a password using AES-GCM.
     * Expects Base64 encoded string with format: IV:EncryptedData
     */
    fun decrypt(encrypted: String, key: SecretKey): String {
        val combined = Base64.decode(encrypted, Base64.NO_WRAP)

        // extract IV and encrypted data
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encryptedData = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    }
}
