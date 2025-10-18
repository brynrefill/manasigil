package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * settings page with app configuration options.
 *
 * @param onBackClick - callback when back button is clicked
 */
@Composable
fun SettingsPage(
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize() // ?
            .background(Color(0xFF673AB7)) // set purple background
    ) {
        // back button in top right corner
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                modifier = Modifier.padding(top = 60.dp),
                imageVector = Icons.Filled.Close,
                contentDescription = "Close page",
                tint = Color.White
            )
        }

        // content
        Column(
            modifier = Modifier
                .fillMaxSize() // ?
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start // ?
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // title
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "Settings",
                fontSize = 32.sp,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // setting items
            SettingItem(
                title = "Account",
                description = "Manage your account"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingItem(
                title = "Security",
                description = "Password and access options"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingItem(
                title = "Backup",
                description = "Backup and restore your data"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingItem(
                title = "Theme",
                description = "Customize app appearance"
            )
        }
    }
}

/**
 * single setting item in the list.
 *
 * @param title - title of the setting item
 * @param description - brief description of the setting item
 */
@Composable
fun SettingItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF424242))
            .clickable {
                // TODO: handle setting item logic
            }
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = title,
            fontSize = 18.sp,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = description,
            fontFamily = MontserratFontFamily,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
