package com.brynrefill.manasigil.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.components.LoadingAnimation
import com.brynrefill.manasigil.ui.theme.ManasigilTheme

@Preview(showBackground = true)
@Composable
fun LoadingAnimationPreview() {
    ManasigilTheme {
        LoadingAnimation()
    }
}
