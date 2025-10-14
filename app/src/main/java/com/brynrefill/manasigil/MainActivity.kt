package com.brynrefill.manasigil

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.brynrefill.manasigil.ui.components.CredentialData
import com.brynrefill.manasigil.ui.components.EncryptionHelper
import com.brynrefill.manasigil.ui.components.LoadingAnimation
import com.brynrefill.manasigil.ui.pages.CreateAccountPage
import com.brynrefill.manasigil.ui.pages.HelpPage
import com.brynrefill.manasigil.ui.pages.Homepage
import com.brynrefill.manasigil.ui.pages.SettingsPage
import com.brynrefill.manasigil.ui.pages.SignInPage
import com.brynrefill.manasigil.ui.pages.WelcomePage
import com.brynrefill.manasigil.ui.theme.ManasigilTheme
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import javax.crypto.SecretKey

/**
 * the main entry point of Manasigil. This class extends ComponentActivity,
 * which is the base class for activities that use Jetpack Compose for building the UI
 */
class MainActivity : FragmentActivity() { // : ComponentActivity() {
    // Firebase Authentication instance
    // lateinit means that it will be initialized later in onCreate
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var encryptionKey: SecretKey

    // state to track which screen to show
    private var currentPage = mutableStateOf("home")

    // state to store the logged-in user's email, considered as username
    private var loggedInUsername = mutableStateOf("")

    // states for biometric authentication
    private var isBiometricAuthenticated = mutableStateOf(false)
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    /**
     * onCreate is called when the activity is first created.
     * This is where the UI is set up using Jetpack Compose
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // make the UI of the app extend into the system UI areas

        // Authenticate with Firebase using Password-Based Accounts on Android:
        // https://firebase.google.com/docs/auth/android/password-auth
        // initialize Firebase Authentication
        auth = Firebase.auth

        // initialize Firestore db
        db = Firebase.firestore

        // initialize encryption key (one-time setup)
        encryptionKey = EncryptionHelper.getKey()

        // biometric authentication setup
        setupBiometricAuthentication()

        // setContent is a Compose function that sets the content of this activity.
        // Everything inside this block is the UI
        setContent {
            // ManasigilTheme is the custom theme of the app and provides Material Design 3 styling
            ManasigilTheme {

                // remember creates a state (here named currentPage, initialized with "home")
                // that persists across recompositions. Jetpack Compose builds the UI declaratively.
                // When something changes (like a state), Compose “recomposes” the UI, meaning it
                // re-runs the composable functions to update the interface
                // var currentPage by remember { mutableStateOf("home") } // track which screen to show

                // state to control loading screen visibility
                var isLoading by remember { mutableStateOf(true) }

                // state to track if system back button/gesture is triggered
                var backPressedOnce by remember { mutableStateOf(false) }

                // state to store the logged-in user's email, considered as username
                // var loggedInUsername by remember { mutableStateOf("") }

                // state to store if the user is a new user
                var newUser by remember { mutableStateOf(false) }

                // check if user is already signed in when the composable is first created.
                // LaunchedEffect runs once when the composable enters the composition
                LaunchedEffect(Unit) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // user is signed in
                        loggedInUsername.value = currentUser.email ?: ""
                        currentPage.value = "welcome"
                        // trigger biometric authentication
                        // showBiometricPrompt()
                    }
                    // hide loading screen after 2 seconds (enough time for animation)
                    kotlinx.coroutines.delay(2000)
                    isLoading = false

                    // trigger biometric authentication after loading animation finishes
                    if (currentUser != null) {
                        showBiometricPrompt()
                    }
                }

                // BackHandler intercepts the system back button/gesture
                BackHandler(enabled = true) {
                    when (currentPage.value) {
                        "home", "welcome" -> {
                            // on homepage
                            if (backPressedOnce) {
                                // if already triggered, triggering it a second time
                                // (within two seconds) will close the app
                                auth.signOut() // if the user is logged in, sign out too
                                loggedInUsername.value = ""
                                isBiometricAuthenticated.value = false
                                newUser = false
                                // currentPage.value = "home"
                                backPressedOnce = false
                                finish()
                            } else {
                                // if not already triggered
                                backPressedOnce = true // set flag
                                Toast.makeText( // show message
                                    this@MainActivity,
                                    "Go back again to exit!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // reset the flag after 2 seconds.
                                // This prevents accidental exits if user waits too long
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    backPressedOnce = false
                                }, 2000)
                            }
                        }
                        "createaccount", "signin" -> {
                            // on other pages, just go back to the homepage
                            currentPage.value = "home"
                            backPressedOnce = false // reset flag
                        }
                        "help", "settings" -> {
                            currentPage.value = "welcome"
                            backPressedOnce = false
                        }
                        /*
                        "welcome" -> {
                            // on welcome page, sign out and go to home
                            auth.signOut()
                            loggedInUsername = ""
                            newUser = false
                            currentPage = "home"
                            backPressedOnce = false
                        }
                        */
                    }
                }

                // Surface is a container to style the UI
                Surface(
                    modifier = Modifier.fillMaxSize(), // make it take up the entire screen
                    color = MaterialTheme.colorScheme.background // set background color
                ) {
                    /*
                    // show loading animation
                    if (isLoading) {
                        LoadingAnimation()
                    } else {}
                    */

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
                                    // validate inputs before creating account
                                    when {
                                        email.isEmpty() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter an email address!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter a valid email address!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        password.isEmpty() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter a password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        password.length < 8 -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must be at least 8 characters!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !password.matches(Regex(".*[A-Z].*")) -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must contain at least one uppercase letter!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !password.matches(Regex(".*[a-z].*")) -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must contain at least one lowercase letter!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !password.matches(Regex(".*\\d.*")) -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must contain at least one number!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")) -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must contain at least one special character!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        password.contains(" ") -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Password must not contain spaces!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        repeatPassword.isEmpty() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please confirm your password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        password != repeatPassword -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Passwords do not match!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        // all validation checks passed
                                        else -> {
                                            // create account with Firebase Authentication
                                            auth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this@MainActivity) { task ->
                                                    if (task.isSuccessful) {
                                                        // account creation successful
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Account created successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        // navigate to welcome page and update UI with the logged-in user's username
                                                        Log.d(TAG, "createUserWithEmail:success")
                                                        val currentUser = auth.currentUser
                                                        loggedInUsername.value = currentUser!!.email
                                                            ?: "" // only non-null asserted calls allowed
                                                        newUser = true
                                                        currentPage.value = "welcome" // updateUI(user)
                                                    } else {
                                                        // if account creation fails, display a message to the user
                                                        Log.w(
                                                            TAG,
                                                            "createUserWithEmail:failure",
                                                            task.exception
                                                        )
                                                        Toast.makeText(
                                                            // baseContext,
                                                            this@MainActivity,
                                                            "Authentication failed. ${task.exception?.message}", // error message
                                                            Toast.LENGTH_LONG
                                                            // Toast.LENGTH_SHORT
                                                        ).show()
                                                        // updateUI(null)
                                                    }
                                                }
                                        }
                                    }
                                }
                            )

                            "signin" -> SignInPage(
                                onBackClick = { currentPage.value = "home" },
                                onSignIn = { email, password ->
                                    // validate inputs before signing in
                                    when {
                                        email.isEmpty() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter an email address!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter a valid email address!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        password.isEmpty() -> {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please enter a password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        // all validation checks passed
                                        else -> {
                                            // sign in with Firebase Authentication
                                            auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this@MainActivity) { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Signed in successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        // navigate to welcome page and update UI with the logged-in user's username
                                                        Log.d(TAG, "signInWithEmail:success")
                                                        val currentUser = auth.currentUser
                                                        loggedInUsername.value = currentUser!!.email
                                                            ?: "" // only non-null asserted calls allowed
                                                        currentPage.value = "welcome" // updateUI(user)
                                                    } else {
                                                        // if sign-in fails, display a message to the user
                                                        Log.w(
                                                            TAG,
                                                            "signInWithEmail:failure",
                                                            task.exception
                                                        )
                                                        Toast.makeText(
                                                            // baseContext,
                                                            this@MainActivity,
                                                            "Authentication failed. ${task.exception?.message}", // error message
                                                            Toast.LENGTH_LONG
                                                            // Toast.LENGTH_SHORT
                                                        ).show()
                                                        // updateUI(null)
                                                    }
                                                }
                                        }
                                    }
                                }

                            )

                            "welcome" -> {
                                if (isBiometricAuthenticated.value) {
                                    WelcomePage(
                                        username = loggedInUsername.value,
                                        isNew = newUser,
                                        onLogout = {
                                            auth.signOut()
                                            // because the state would persist in the composable
                                            loggedInUsername.value = ""
                                            isBiometricAuthenticated.value = false  // reset biometric state
                                            newUser = false
                                            currentPage.value = "home"
                                            backPressedOnce = false
                                        },
                                        onHelpClick = { currentPage.value = "help" },
                                        onSettingsClick = { currentPage.value = "settings" },
                                        onLoadCredentials = { callback ->
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                loadCredentials(
                                                    userId,
                                                    onSuccess = callback,
                                                    onFailure = { exception ->
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error loading credential items: ${exception.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        callback(emptyList())
                                                    }
                                                )
                                            } else {
                                                callback(emptyList())
                                            }
                                        },
                                        onAddCredential = { credential, onSuccess/*, onFailure*/ ->
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                // addCredential(userId, credential, onSuccess, onFailure)
                                                addCredential(
                                                    userId,
                                                    credential,
                                                    {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Credentials added successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        onSuccess()
                                                    },
                                                    { exception ->
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error adding credentials: ${exception.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            } else {
                                                // onFailure(Exception("You are not logged in!"))
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "You are not logged in!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onUpdateCredential = { credential, onSuccess/*, onFailure*/ ->
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                // updateCredential(userId, credential, onSuccess, onFailure)
                                                updateCredential(
                                                    userId,
                                                    credential,
                                                    {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Credentials updated successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        onSuccess()
                                                    },
                                                    { exception ->
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error updating credentials: ${exception.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            } else {
                                                // onFailure(Exception("You are not logged in!"))
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "You are not logged in!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onDeleteCredential = { documentId, onSuccess/*, onFailure*/ ->
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                // deleteCredential(userId, documentId, onSuccess, onFailure)
                                                deleteCredential(
                                                    userId,
                                                    documentId,
                                                    {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Credentials deleted successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        onSuccess()
                                                    },
                                                    { exception ->
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error deleting credentials: ${exception.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            } else {
                                                // onFailure(Exception("You are not logged in!"))
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "You are not logged in!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                } else {
                                    // show biometric prompt page
                                    // TODO: refactor it creating BiometricAuthDialog()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF673AB7)), // set purple background
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(bottom = 24.dp),
                                                text = "Authenticate to continue!",
                                                fontSize = 20.sp,
                                                fontFamily = MontserratFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Button(
                                                onClick = { showBiometricPrompt() },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF373434) // set gray color
                                                ),
                                                shape = RoundedCornerShape(0.dp)
                                            ) {
                                                Text(
                                                    text = "UNLOCK",
                                                    fontFamily = MontserratFontFamily,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
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
    }

    // BIOMETRIC AUTH functions
    private fun setupBiometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    /*
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                    */
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    /*
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    */
                    isBiometricAuthenticated.value = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    /*
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                    */
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to access your Manasigil credentials!")
            .setSubtitle("Please unlock to proceed.") // using fingerprint (biometric) or device credentials auth

            // for API < 30 i.e. Build.VERSION.SDK_INT >= Build.VERSION_CODES.R it's not possible to "OR-ing" two different authenticators
            /*
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            */

            // .setNegativeButtonText(...) not compatible with DEVICE_CREDENTIAL. The system provides its own UI for device credentials -> the system will crash!
            // TODO: check if with a device that not support BIOMETRIC_STRONG it will crash with this istruction and manage all the possible cases
            .setNegativeButtonText("CANCEL")
            .build()
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        // val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
        //                      BiometricManager.Authenticators.DEVICE_CREDENTIAL

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        //when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // biometric authentication available
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // no biometric hardware, use device credentials (PIN/Pattern/Password)
                Toast.makeText(
                    this,
                    "No biometric hardware available! Please use your device PIN/Pattern/Password.",
                    Toast.LENGTH_LONG
                ).show()
                // not allow access without biometric
                // isBiometricAuthenticated.value = true
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // biometric hardware unavailable, use device credentials
                Toast.makeText(
                    this,
                    "Biometric hardware unavailable! Please use your device PIN/Pattern/Password.",
                    Toast.LENGTH_LONG
                ).show()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // no biometrics enrolled, ask to set it up
                Toast.makeText(
                    this,
                    "No fingerprints enrolled. Please add a new one in device settings!",
                    Toast.LENGTH_LONG
                ).show()
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                // fallback to device credentials
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    // CRUD OPERATIONS
    /**
     * get all credential items from Firestore db for the current user
     */
    private fun loadCredentials(
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
    private fun addCredential(
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
    private fun updateCredential(
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
    private fun deleteCredential(
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
