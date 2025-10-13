package com.brynrefill.manasigil.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brynrefill.manasigil.ui.theme.MontserratFontFamily

/**
 * help page with info for users.
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
                .align(Alignment.TopEnd)
                // .align(Alignment.TopStart)
                // .padding(16.dp)
        ) {
            Icon(
                modifier = Modifier.padding(top = 60.dp), // add space above back button
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
            horizontalAlignment = Alignment.Start,
            // horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // 60.dp

            // title
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Need help?",
                fontSize = 32.sp, // 36.sp
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            // intro section
            InfoSection(
                title = "Introduction",
                content = "Manasigil is a user-friendly and cloud-based password manager, designed to help you manage your credentials easily and safely."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // features section
            InfoSection(
                title = "Key features",
                items = listOf(
                    InfoItem("SECURE STORAGE", Icons.Filled.Shield, "All your credentials and notes are encrypted locally using AES-256-GCM encryption with keys stored in the Android Keystore, before being sent and stored in the cloud. Manasigil also can't access your credentials."),
                    // InfoItem("BIOMETRIC AUTHENTICATION", "Access your credentials with fingerprint for quick and secure login."),
                    InfoItem("BIOMETRIC UNLOCK", Icons.Filled.Lock, "Quickly and securely unlock the app using your fingerprint to access your credentials."),
                    InfoItem("PASSWORD GENERATION", Icons.Filled.Refresh, "Generate strong passwords that are hard to crack."),
                    InfoItem("BREACH DETECTION", Icons.Filled.Check, "Check if your passwords have been compromised in known data breaches."),
                    InfoItem("PASSWORD AGE TRACKING", Icons.Filled.TrackChanges, "Visual indicators remind you to update old passwords:\n" +
                    "🟢 password is fresh\n" + // (< 5 months)
                    "🟠 consider updating\n" + // (≥ 5 months)
                    "🔴 update recommended"), // (≥ 6 months)
                    // ?
                    InfoItem("CLOUD SYNC", Icons.Filled.Cloud, "Your encrypted credentials are synced across devices.")
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // contacts section
            InfoSection(
                title = "Contact us",
                content = "Questions or feedback? Let us know at: info@brynrefill.com. We'll appreciate!!"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // footer section
            Column(
                modifier = Modifier.fillMaxWidth(), // ??
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Divider is deprectated insted
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.3f)
                )

                Text(
                    text = "Version 1.0.0", // ?
                    fontSize = 12.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Text(
                    text = "© 2025 Manasigil. All rights reserved.",
                    fontSize = 12.sp,
                    fontFamily = MontserratFontFamily,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * reusable section component
 */
@Composable
fun InfoSection(
    title: String,
    content: String? = null,
    items: List<InfoItem>? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // ?
            .background(Color(0xFF424242))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (content != null) {
            Text(
                text = content,
                fontSize = 14.sp,
                fontFamily = MontserratFontFamily,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify,
                letterSpacing = 1.sp
            )
        }

        /*
        if (items != null) {
            items.forEach { item ->
                InfoItemRow(item)
            }
        }
        */
        items?.forEach { item ->
            InfoItemRow(item)
        }
    }
}

/**
 * data class for info items
 */
data class InfoItem(
    val title: String,
    val icon: ImageVector,
    val description: String
)

/**
 * single info item
 */
@Composable
fun InfoItemRow(item: InfoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // ??
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start // ?
    ) {
        Column {
            if (item.title.isNotEmpty()) {
                Row(
                    // verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    // modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "" + item.icon + " icon",
                        tint = Color.White
                    )
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            Text(
                text = item.description,
                fontSize = 13.sp, // 16.sp
                fontFamily = MontserratFontFamily,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 18.sp, // 24.sp
                textAlign = TextAlign.Justify,
                letterSpacing = 1.sp
            )
        }
    }
}
