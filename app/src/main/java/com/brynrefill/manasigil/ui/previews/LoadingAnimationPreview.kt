package com.brynrefill.manasigil.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.brynrefill.manasigil.ui.components.LoadingAnimation
import com.brynrefill.manasigil.ui.theme.ManasigilTheme

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
