package com.brynrefill.manasigil.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.pages.SignInPage
import com.brynrefill.manasigil.ui.theme.ManasigilTheme

@Preview(showBackground = true)
@Composable
fun SignInPagePreview() {
    ManasigilTheme {
        SignInPage()
    }
}
