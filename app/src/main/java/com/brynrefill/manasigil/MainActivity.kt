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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.brynrefill.manasigil.ui.theme.ManasigilTheme

/**
 * this loads the Montserrat font files from the res/font folder and
 * creates a personalized font family to use throughout the code (UI components)
 */
val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

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
                                isNew = newUser
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

/**
 * create account page with account creation form.
 *
 * @param onBackClick - callback function when back button is clicked
 * @param onCreateAccount - callback function when submit button is clicked
 */
@Composable
fun CreateAccountPage(
    onBackClick: () -> Unit = {},
    onCreateAccount: (String, String, String) -> Unit = { _, _, _ -> }
) {
    // state variables to hold the text field values
    // remember and mutableStateOf allows the UI to update when values change
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    // Box allows to overlay the back button on top of the content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)) // set purple background
    ) {
        // back button in top left corner
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart) // ?
                // .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above back button
                text = "X",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                // .background(Color(0xFF673AB7)) // set purple background
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // app name (logo)
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Manasigil",
                fontSize = 36.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // call to action text
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "Create your Manasigil account.",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )

            // e-mail text field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it }, // update email state when text changes
                placeholder = {
                    Text(
                        text = "E-MAIL",
                        color = Color.White.copy(alpha = 0.6f) // set semi-transparent white color
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // ?
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434), // set dark gray background
                    unfocusedContainerColor = Color(0xFF373434), // set dark gray background
                    focusedBorderColor = Color(0xFF373434), // set dark gray border
                    unfocusedBorderColor = Color(0xFF373434) // set dark gray border
                ),
                shape = RoundedCornerShape(0.dp), // ? // sharp corners
                singleLine = true // keep text on one line
            )

            // password text field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it }, // update password state when text changes
                placeholder = {
                    Text(
                        text = "PASSWORD",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(), // hide password with dots
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434),
                    unfocusedContainerColor = Color(0xFF373434),
                    focusedBorderColor = Color(0xFF373434),
                    unfocusedBorderColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                singleLine = true
            )

            // repeat password text field
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it }, // update repeatPassword state when text changes
                placeholder = {
                    Text(
                        text = "REPEAT PASSWORD",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434),
                    unfocusedContainerColor = Color(0xFF373434),
                    focusedBorderColor = Color(0xFF373434),
                    unfocusedBorderColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                singleLine = true
            )

            // submit button
            Button(
                onClick = {
                    // call the callback with the form data
                    onCreateAccount(email, password, repeatPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF424242) // set light gray background
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // bottom section with footer
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above footer
                text = "© 2025 brynrefill.com",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }
    }
}

/**
 * sign in page with log in form.
 *
 * @param onBackClick - callback function when back button is clicked
 * @param onSignIn - callback function when submit button is clicked
 */
@Composable
fun SignInPage(
    onBackClick: () -> Unit = {},
    onSignIn: (String, String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)) // set purple background
    ) {
        // back button in top left corner
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart) // ?
                // .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above back button
                text = "X",
                fontSize = 24.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // app name (logo)
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Manasigil",
                fontSize = 36.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // call to action text
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "Sign in to your Manasigil account.",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )

            // e-mail text field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it }, // update email state when text changes
                placeholder = {
                    Text(
                        text = "E-MAIL",
                        color = Color.White.copy(alpha = 0.6f) // set semi-transparent white
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // ?
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434), // set dark gray background
                    unfocusedContainerColor = Color(0xFF373434), // set dark gray background
                    focusedBorderColor = Color(0xFF373434), // set dark gray border
                    unfocusedBorderColor = Color(0xFF373434) // set dark gray border
                ),
                shape = RoundedCornerShape(0.dp), // ? // sharp corners
                singleLine = true // keep text on one line
            )

            // password text field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it }, // update password state when text changes
                placeholder = {
                    Text(
                        text = "PASSWORD",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(), // hide password with dots
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // add padding before button
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF373434),
                    unfocusedContainerColor = Color(0xFF373434),
                    focusedBorderColor = Color(0xFF373434),
                    unfocusedBorderColor = Color(0xFF373434)
                ),
                shape = RoundedCornerShape(0.dp), // ?
                singleLine = true
            )

            // submit button
            Button(
                onClick = {
                    // call the callback with the form data
                    onSignIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF424242) // set light gray background
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // bottom section with footer
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above footer
                text = "© 2025 brynrefill.com",
                fontSize = 15.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }
    }
}

/**
 * welcome page shown after successful account creation or sign in.
 *
 * @param username - the username of the registered/logged-in user
 * @param isNew - state if the user is a new user
 */
@Composable
fun WelcomePage(
    username: String,
    isNew: Boolean
) {
    // remember the scroll state of the credentials list, when content overflows
    val scrollState = rememberScrollState()

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
                    onClick = {
                        // TODO: handle add a credential in the credentials list logic
                    },
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
                    onClick = {
                        // TODO: handle log out logic
                    },
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

            // TODO: handle the live insertion of a credential item in the list logic
            CredentialItem(label = "<credential1>")
            Spacer(modifier = Modifier.height(16.dp))
            CredentialItem(label = "<credential2>")
            Spacer(modifier = Modifier.height(16.dp))
            CredentialItem(label = "<credential3>")
        }
    }
}

/**
 * a single credential item.
 *
 * @param label - string that identify a credential item in the list, e.g. <service>.com
 */
@Composable
fun CredentialItem(
    label: String
) {
    // state to track if the item is expanded
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()  // ?
    ) {
        // (always visible) togglable row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFF424242))
                .clickable { isExpanded = !isExpanded }, // toggle expansion on click
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
                    text = "Username: <email>",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )

                // password text
                Text(
                    text = "Password: <password>",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White
                )

                // notes text
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Notes: <notes>",
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
                        onClick = {
                            // TODO: handle deletion credential item logic
                        },
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

/**
 * the homepage with app title, slogan, create account and sign in buttons and footer with copyright.
 *
 * @param onSignInClick - callback function when sign in button is clicked
 * @param onCreateAccountClick - callback function when create account button is clicked
 */
@Composable
fun Homepage(
    onSignInClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    // Column arranges its children vertically
    Column(
        modifier = Modifier
            .fillMaxSize() // fill the entire screen
            .background(Color(0xFF673AB7)) // set background color
            .padding(32.dp), // add padding around all sides
        horizontalAlignment = Alignment.CenterHorizontally, // center children horizontally
        verticalArrangement = Arrangement.Center // center everything vertically on screen
    ) {
        // top section with app title and slogan
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 48.dp) // add space below logo/slogan
        ) {
            // app name (logo)
            Text(
                text = "Manasigil",
                fontSize = 36.sp, // sp = scalable pixels
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // slogan
            Text(
                text = "A trusted sigil for your solid credentials.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }

        // middle section with buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth() // make this column fill the width
        ) {
            // create account button
            Button(
                onClick = onCreateAccountClick, // call the navigation callback
                modifier = Modifier
                    .fillMaxWidth() // make button full width
                    .height(56.dp), // set button height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434) // set dark gray background
                ),
                shape = RoundedCornerShape(0.dp) // sharp corners (0dp radius = no rounding)
            ) {
                // button text
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // add space between buttons

            // sign in button
            Button(
                onClick = onSignInClick, // call the navigation callback
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF673AB7) // set purple background
                ),
                border = BorderStroke(2.dp, Color(0xFF373434)), // set button border
                shape = RoundedCornerShape(0.dp)
            ) {
                // button text
                Text(
                    text = "SIGN IN",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }

        // bottom section with footer
        Text(
            modifier = Modifier.padding(top = 40.dp), // add space above footer
            text = "© 2025 brynrefill.com",
            fontSize = 15.sp,
            fontFamily = MontserratFontFamily,
            color = Color.White
        )
    }
}

/**
 * the preview function allows to see the UI in Android Studio
 * without running the app on a device or emulator.
 *
 * The @Preview annotation tells Android Studio to render this function
 * in the design preview panel
 */
@Preview(showBackground = true)
@Composable
fun LoadingAnimationPreview() {
    ManasigilTheme {
        LoadingAnimation()
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    ManasigilTheme { // MaterialTheme {
        Homepage()
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountPagePreview() {
    ManasigilTheme {
        CreateAccountPage()
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPagePreview() {
    ManasigilTheme {
        SignInPage()
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePagePreview() {
    ManasigilTheme {
        WelcomePage("example@example.com", true)
    }
}

@Preview(showBackground = true)
@Composable
fun CredentialItemPreview() {
    ManasigilTheme {
        CredentialItem("Google.com")
    }
}
