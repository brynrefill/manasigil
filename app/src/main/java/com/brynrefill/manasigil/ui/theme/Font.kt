package com.brynrefill.manasigil.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.brynrefill.manasigil.R

/**
 * this loads the Montserrat font files from the res/font folder and
 * creates a personalized font family to use throughout the code (UI components)
 */
val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)
