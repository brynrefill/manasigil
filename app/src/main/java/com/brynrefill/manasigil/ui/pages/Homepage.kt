package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

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
