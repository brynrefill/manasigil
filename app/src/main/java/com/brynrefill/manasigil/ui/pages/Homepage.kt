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
 * the homepage with app title, "slogan", create account and sign in buttons and footer with copyright.
 *
 * @param onSignInClick - callback function when sign in button is clicked
 * @param onCreateAccountClick - callback function when create account button is clicked
 */
@Composable
fun Homepage(
    onSignInClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)) // set background color
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // app title and "slogan"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            // app name (logo)
            Text(
                text = "Manasigil",
                fontSize = 36.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // "slogan"
            Text(
                text = "A managed sigil for your solid credentials.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }

        // buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // CREATE ACCOUNT button
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF373434) // set dark gray background
                ),
                shape = RoundedCornerShape(0.dp) // sharp corners (0dp radius = no rounding)
            ) {
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SIGN IN button
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF673AB7) // set purple background
                ),
                border = BorderStroke(2.dp, Color(0xFF373434)), // set button border
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = "SIGN IN",
                    fontSize = 16.sp,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }

        // footer
        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = "© 2025 brynrefill.com",
            fontSize = 15.sp,
            fontFamily = MontserratFontFamily,
            color = Color.White
        )
    }
}
