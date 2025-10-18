package com.brynrefill.manasigil

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.brynrefill.manasigil.data.model.CredentialData
import com.brynrefill.manasigil.data.repository.CredentialRepository
import com.brynrefill.manasigil.ui.components.LoadingAnimation
import com.brynrefill.manasigil.ui.pages.BiometricPromptPage
import com.brynrefill.manasigil.ui.pages.CreateAccountPage
import com.brynrefill.manasigil.ui.pages.HelpPage
import com.brynrefill.manasigil.ui.pages.Homepage
import com.brynrefill.manasigil.ui.pages.SettingsPage
import com.brynrefill.manasigil.ui.pages.SignInPage
import com.brynrefill.manasigil.ui.pages.WelcomePage
import com.brynrefill.manasigil.ui.theme.ManasigilTheme
import com.brynrefill.manasigil.utils.BiometricHelper
import com.brynrefill.manasigil.utils.ClipboardHelper
import com.brynrefill.manasigil.utils.EncryptionHelper
import com.brynrefill.manasigil.utils.QRCodeParser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import javax.crypto.SecretKey

/**
 * the main entry point of Manasigil.
 *
 * Responsibilities:
 * - initialize Firebase and encryption
 * - manage app authentication
 * - handle biometric local authentication
 * - handle camera permissions
 * - handle navigation between pages
 * - coordinate between UI and data layers
 */
class MainActivity : FragmentActivity() {
    // Firebase Authentication and Cloud Firestore instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // encryption
    private lateinit var encryptionKey: SecretKey

    // repository
    private lateinit var credentialRepository: CredentialRepository

    // helpers
    private lateinit var biometricHelper: BiometricHelper
    private lateinit var clipboardHelper: ClipboardHelper
    private lateinit var qrCodeParser: QRCodeParser

    // navigation states
    // state to track which screen to show
    private var currentPage = mutableStateOf("home")
    // state to store the logged-in user's email, considered as username
    private var loggedInUsername = mutableStateOf("")
    // state to store if the user is a new user
    private var newUser = mutableStateOf(false)

    // state for biometric authentication
    private var isBiometricAuthenticated = mutableStateOf(false)

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    /**
     * onCreate is called when the activity is first created.
     * This is where the UI is set up using Jetpack Compose
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // make the UI of the app extend into the system UI areas
        enableEdgeToEdge()

        // Authenticate with Firebase using password-based accounts
        // initialize Firebase Authentication and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // initialize encryption key (one-time setup)
        encryptionKey = EncryptionHelper.getKey()

        // initialize repository
        credentialRepository = CredentialRepository(db, encryptionKey)

        // initialize helpers
        biometricHelper = BiometricHelper(this)
        clipboardHelper = ClipboardHelper(this)
        qrCodeParser = QRCodeParser()

        // biometric authentication setup
        biometricHelper.setup(
            onAuthenticationSucceeded = {
                isBiometricAuthenticated.value = true
                Toast.makeText(this, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
            },
            onAuthenticationError = { errorMsg ->
                Toast.makeText(this, "Authentication error: $errorMsg", Toast.LENGTH_SHORT).show()
                if (auth.currentUser != null) {
                    handleLogout()
                }
            },
            onAuthenticationFailed = {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        )

        // set the content of this activity.
        // Everything inside this block is the UI
        setContent {
            MainScreen()
        }
    }

    /**
     * main screen with navigation logic
     */
    @Composable
    private fun MainScreen() {
        // ManasigilTheme is the custom theme of the app
        ManasigilTheme {
            // state to control loading animation visibility
            var isLoading by remember { mutableStateOf(true) }

            // state to track if system back button/gesture is triggered
            var backPressedOnce by remember { mutableStateOf(false) }

            // check if user is already signed in when the composable is first created.
            // LaunchedEffect runs once when the composable enters the composition
            LaunchedEffect(Unit) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // user is signed in
                    loggedInUsername.value = currentUser.email ?: ""
                    currentPage.value = "welcome"
                }
                // hide loading animation after 2 seconds
                kotlinx.coroutines.delay(2000)
                isLoading = false

                // trigger biometric authentication after loading animation finishes
                if (currentUser != null) {
                    biometricHelper.showPrompt()
                }
            }

            // handle back button
            // BackHandler intercepts the system back button/gesture
            BackHandler(enabled = true) {
                handleBackPress(backPressedOnce) { backPressedOnce = it }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background // set background color
            ) {
                // show main content immediately (no fade in)
                if (!isLoading) {
                    // show different pages (i.e. mobile screens) based on currentPage state
                    when (currentPage.value) {
                        "home" -> Homepage(
                            onCreateAccountClick = { currentPage.value = "createaccount" },
                            onSignInClick = { currentPage.value = "signin" }
                        )

                        "createaccount" -> CreateAccountPage(
                            onBackClick = { currentPage.value = "home" },
                            onCreateAccount = { email, password, repeatPassword ->
                                handleCreateAccount(email, password, repeatPassword)
                            }
                        )

                        "signin" -> SignInPage(
                            onBackClick = { currentPage.value = "home" },
                            onSignIn = { email, password -> handleSignIn(email, password) }
                        )

                        "welcome" -> {
                            if (isBiometricAuthenticated.value) {
                                WelcomePage(
                                    username = loggedInUsername.value,
                                    isNew = newUser.value,
                                    onLogout = { handleLogout() },
                                    onHelpClick = { currentPage.value = "help" },
                                    onSettingsClick = { currentPage.value = "settings" },
                                    onLoadCredentials = { callback -> loadCredentials(callback) },
                                    onAddCredential = { credential, onSuccess/*, onFailure*/ -> addCredential(credential, onSuccess) },
                                    onUpdateCredential = { credential, onSuccess/*, onFailure*/ -> updateCredential(credential, onSuccess) },
                                    onDeleteCredential = { documentId, onSuccess/*, onFailure*/ -> deleteCredential(documentId, onSuccess) },
                                    onCopyToClipboard = { label, text -> clipboardHelper.copy(label, text) },
                                    parseQRCode = { rawValue -> qrCodeParser.parse(rawValue) }
                                )
                            } else {
                                BiometricPromptPage(
                                    onUnlockClick = { biometricHelper.showPrompt() }
                                )
                            }
                        }
                        "help" -> HelpPage(
                            onBackClick = { currentPage.value = "welcome" }
                        )

                        "settings" -> SettingsPage(
                            onBackClick = { currentPage.value = "welcome" }
                        )
                    }
                }

                // show loading animation on top with fade out
                AnimatedVisibility(
                    visible = isLoading,
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    LoadingAnimation()
                }
            }
        }
    }

    /**
     * handle back button press logic
     */
    private fun handleBackPress(backPressedOnce: Boolean, updateBackPressed: (Boolean) -> Unit) {
        when (currentPage.value) {
            "home"/*, "welcome"*/ -> {
                // on homepage
                if (backPressedOnce) {
                    // if already triggered, triggering it a second time
                    // (within two seconds) will close the app
                    finish()
                } else {
                    // if not already triggered
                    updateBackPressed(true)
                    Toast.makeText(this, "Go back again to exit!", Toast.LENGTH_SHORT).show()

                    // reset the flag after 2 seconds.
                    // This prevents accidental exits if user waits too long
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        updateBackPressed(false)
                    }, 2000)
                }
            }
            "createaccount", "signin" -> {
                // on other pages, just go back to the homepage
                currentPage.value = "home"
                updateBackPressed(false)
            }
            "help", "settings" -> {
                currentPage.value = "welcome"
                updateBackPressed(false)
            }
            "welcome" -> {
                // on welcome page, sign out and go to home
                handleLogout()
                updateBackPressed(false)
            }
        }
    }

    /**
     * handle account creation
     */
    private fun handleCreateAccount(email: String, password: String, repeatPassword: String) {
        // validate inputs before creating account
        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Please enter an email address!", Toast.LENGTH_SHORT).show()
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show()
            }

            password.isEmpty() -> {
                Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
            }

            password.length < 8 -> {
                Toast.makeText(this, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show()
            }

            !password.matches(Regex(".*[A-Z].*")) -> {
                Toast.makeText(this, "Password must contain at least one uppercase letter!", Toast.LENGTH_SHORT).show()
            }

            !password.matches(Regex(".*[a-z].*")) -> {
                Toast.makeText(this, "Password must contain at least one lowercase letter!", Toast.LENGTH_SHORT).show()
            }

            !password.matches(Regex(".*\\d.*")) -> {
                Toast.makeText(this, "Password must contain at least one number!", Toast.LENGTH_SHORT).show()
            }

            !password.matches(Regex(".*[!@#$%^&*(),.?\":{}|<>].*")) -> {
                Toast.makeText(this, "Password must contain at least one special character!", Toast.LENGTH_SHORT).show()
            }

            password.contains(" ") -> {
                Toast.makeText(this, "Password must not contain spaces!", Toast.LENGTH_SHORT).show()
            }

            repeatPassword.isEmpty() -> {
                Toast.makeText(this, "Please confirm your password!", Toast.LENGTH_SHORT).show()
            }

            password != repeatPassword -> {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }
            // all validation checks passed
            else -> {
                // create account with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // account creation successful
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                            // navigate to welcome page and update UI with the logged-in user's username
                            Log.d(TAG, "createUserWithEmail:success")
                            val currentUser = auth.currentUser
                            loggedInUsername.value = currentUser!!.email ?: "" // only non-null asserted calls allowed
                            currentPage.value = "welcome" // updateUI(user)
                            // newUser.value = true
                            // biometricHelper.showPrompt()
                        } else {
                            // if account creation fails, display a message to the user
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            // updateUI(null)
                        }
                    }
            }
        }
    }

    /**
     * handle user sign in
     */
    private fun handleSignIn(email: String, password: String) {
        // validate inputs before signing in
        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Please enter an email address!", Toast.LENGTH_SHORT).show()
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show()
            }

            password.isEmpty() -> {
                Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
            }
            // all validation checks passed
            else -> {
                // sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show()

                            // navigate to welcome page and update UI with the logged-in user's username
                            Log.d(TAG, "signInWithEmail:success")
                            val currentUser = auth.currentUser
                            loggedInUsername.value = currentUser!!.email ?: "" // only non-null asserted calls allowed
                            currentPage.value = "welcome" // updateUI(user)
                            // biometricHelper.showPrompt()
                        } else {
                            // if sign-in fails, display a message to the user
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            // updateUI(null)
                        }
                    }
            }
        }
    }

    /**
     * handle user logout
     */
    private fun handleLogout() {
        auth.signOut()
        // because the state would persist in the composable
        loggedInUsername.value = ""
        newUser.value = false
        isBiometricAuthenticated.value = false // reset biometric state
        currentPage.value = "home"
        // updateBackPressed(false)
    }

    /**
     * load credentials from Firestore
     */
    private fun loadCredentials(callback: (List<CredentialData>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            credentialRepository.loadCredentials(
                userId,
                onSuccess = callback,
                onFailure = { exception ->
                    Toast.makeText(this, "Error loading credential items: ${exception.message}", Toast.LENGTH_LONG).show()
                    callback(emptyList())
                }
            )
        } else {
            callback(emptyList())
        }
    }

    /**
     * add credential to Firestore
     */
    private fun addCredential(
        credential: CredentialData,
        onSuccess: () -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            credentialRepository.addCredential(
                userId,
                credential,
                {
                    Toast.makeText(this, "Credentials added successfully!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                },
                { exception ->
                    Toast.makeText(this, "Error adding credentials: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * update credential in Firestore
     */
    private fun updateCredential(
        credential: CredentialData,
        onSuccess: () -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            credentialRepository.updateCredential(
                userId,
                credential,
                {
                    Toast.makeText(this, "Credentials updated successfully!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                },
                { exception ->
                    Toast.makeText(this, "Error updating credentials: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * delete credential from Firestore
     */
    private fun deleteCredential(documentId: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            credentialRepository.deleteCredential(
                userId,
                documentId,
                {
                    Toast.makeText(this, "Credentials deleted successfully!", Toast.LENGTH_SHORT).show()
                    onSuccess()
                },
                { exception ->
                    Toast.makeText(this, "Error deleting credentials: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * request camera permission
     */
    private fun requestCameraPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * handle camera permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera permission required for QR scanning. Go to settings!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
