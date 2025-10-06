package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
 * help page with tutorial and info for users.
 *
 * @param onBackClick - callback when back button is clicked
 */
@Composable
fun HelpPage(
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize() // ?
            .background(Color(0xFF673AB7)) // set purple background
    ) {
        // back button in top left corner
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                // .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 40.dp), // add space above back button
                text = "X",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }

        // content
        Column(
            modifier = Modifier
                .fillMaxSize() // ?
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            // horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // 60.dp

            // title
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Help",
                fontSize = 32.sp, // 36.sp
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            // tutorial and info
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\n\n" +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n" +
                        "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.\n\n" +
                        "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.",
                fontSize = 16.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}
