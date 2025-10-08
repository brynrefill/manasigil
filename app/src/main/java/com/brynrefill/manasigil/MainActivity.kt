package com.brynrefill.manasigil

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.brynrefill.manasigil.ui.components.LoadingAnimation
import com.brynrefill.manasigil.ui.pages.CreateAccountPage
import com.brynrefill.manasigil.ui.pages.HelpPage
import com.brynrefill.manasigil.ui.pages.Homepage
import com.brynrefill.manasigil.ui.pages.SettingsPage
import com.brynrefill.manasigil.ui.pages.SignInPage
import com.brynrefill.manasigil.ui.pages.WelcomePage
import com.brynrefill.manasigil.ui.theme.ManasigilTheme


/**
 * the main entry point of Manasigil. This class extends ComponentActivity,
 * which is the base class for activities that use Jetpack Compose for building the UI
 */
class MainActivity : ComponentActivity() {
    // Firebase Authentication instance
    // lateinit means that it will be initialized later in onCreate
    private lateinit var auth: FirebaseAuth

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

        // setContent is a Compose function that sets the content of this activity.
        // Everything inside this block is the UI
        setContent {
            // ManasigilTheme is the custom theme of the app and provides Material Design 3 styling
            ManasigilTheme {

                // remember creates a state (here named currentPage, initialized with "home")
                // that persists across recompositions. Jetpack Compose builds the UI declaratively.
                // When something changes (like a state), Compose “recomposes” the UI, meaning it
                // re-runs the composable functions to update the interface
                var currentPage by remember { mutableStateOf("home") } // track which screen to show

                // state to control loading screen visibility
                var isLoading by remember { mutableStateOf(true) }

                // state to track if system back button/gesture is triggered
                var backPressedOnce by remember { mutableStateOf(false) }

                // state to store the logged-in user's email, considered as username
                var loggedInUsername by remember { mutableStateOf("") }

                // state to store if the user is a new user
                var newUser by remember { mutableStateOf(false) }

                // check if user is already signed in when the composable is first created.
                // LaunchedEffect runs once when the composable enters the composition
                LaunchedEffect(Unit) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // user is signed in
                        loggedInUsername = currentUser.email ?: ""
                        currentPage = "welcome"
                    }
                    // hide loading screen after 4 seconds (enough time for animation)
                    kotlinx.coroutines.delay(4000)
                    isLoading = false
                }

                // BackHandler intercepts the system back button/gesture
                BackHandler(enabled = true) {
                    when (currentPage) {
                        "home", "welcome" -> {
                            // on homepage
                            if (backPressedOnce) {
                                // if already triggered, triggering it a second time
                                // (within two seconds) will close the app
                                auth.signOut() // if the user is logged in, sign out too
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
                            currentPage = "home"
                            backPressedOnce = false // reset flag
                        }
                        "help", "settings" -> {
                            currentPage = "welcome"
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
                        when (currentPage) {
                            "home" -> Homepage(
                                onCreateAccountClick = { currentPage = "createaccount" },
                                onSignInClick = { currentPage = "signin" }
                            )

                            "createaccount" -> CreateAccountPage(
                                onBackClick = { currentPage = "home" },
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
                                                        loggedInUsername = currentUser!!.email
                                                            ?: "" // only non-null asserted calls allowed
                                                        newUser = true
                                                        currentPage = "welcome" // updateUI(user)
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
                                onBackClick = { currentPage = "home" },
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
                                                        loggedInUsername = currentUser!!.email
                                                            ?: "" // only non-null asserted calls allowed
                                                        currentPage = "welcome" // updateUI(user)
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

                            "welcome" -> WelcomePage(
                                username = loggedInUsername,
                                isNew = newUser,
                                onLogout = {
                                    auth.signOut()
                                    // because the state would persist in the composable
                                    loggedInUsername = ""
                                    newUser = false
                                    currentPage = "home"
                                    backPressedOnce = false
                                },
                                onHelpClick = { currentPage = "help" },
                                onSettingsClick = { currentPage = "settings" }
                            )

                            "help" -> HelpPage(
                                onBackClick = { currentPage = "welcome" }
                            )

                            "settings" -> SettingsPage(
                                onBackClick = { currentPage = "welcome" }
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
}
