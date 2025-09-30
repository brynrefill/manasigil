package com.brynrefill.manasigil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
    /**
     * onCreate is called when the activity is first created.
     * This is where the UI is set up using Jetpack Compose
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // make the UI of the app extend into the system UI areas

        // setContent is a Compose function that sets the content of this activity.
        // Everything inside this block is the UI
        setContent {
            // ManasigilTheme is the custom theme of the app and provides Material Design 3 styling
            ManasigilTheme {

                // remember creates a state (here named currentScreen initialized with "home")
                // that persists across recompositions. Jetpack Compose builds the UI declaratively.
                // When something changes (like a state), Compose “recomposes” the UI, meaning it
                // re-runs the composable functions to update the interface
                var currentPage by remember { mutableStateOf("home") } // track which screen to show

                // Surface is a container to style the UI
                Surface(
                    modifier = Modifier.fillMaxSize(), // make it take up the entire screen
                    color = MaterialTheme.colorScheme.background // set background color
                ) {
                    // Homepage() // call the composable function that displays the homepage

                    // show different pages (i.e. mobile screens) based on currentPage state
                    when (currentPage) {
                        "home" -> Homepage(
                            onCreateAccountClick = { currentPage = "createaccount" },
                            onSignInClick = { currentPage = "signin" }
                        )
                        "createaccount" -> CreateAccountPage(
                            onBackClick = { currentPage = "home" }
                        )
                        "signin" -> SignInPage(
                            onBackClick = { currentPage = "home" }
                        )
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
 */
@Composable
fun CreateAccountPage(
    onBackClick: () -> Unit = {}
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
                    // TODO: handle account creation logic
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
 */
@Composable
fun SignInPage(
    onBackClick: () -> Unit = {}
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
                    // TODO: handle log in logic
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
