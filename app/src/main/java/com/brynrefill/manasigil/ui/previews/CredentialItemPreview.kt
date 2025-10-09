package com.brynrefill.manasigil.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.components.CredentialItem
import com.brynrefill.manasigil.ui.theme.ManasigilTheme

@Preview(showBackground = true)
@Composable
fun CredentialItemPreview() {
    ManasigilTheme {
        CredentialItem(
            "Google.com",
            "johndoe@example.com",
            "123456",
            "I chose a perfect password, didn't I?",
            0,
            true,
            true,
            {},
            {}
        )
    }
}
