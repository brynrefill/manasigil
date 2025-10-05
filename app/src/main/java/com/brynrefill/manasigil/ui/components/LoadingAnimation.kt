package com.brynrefill.manasigil.ui.components

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * the animated loading screen shown at app startup.
 * Displays three squares in a row that animate by growing and shrinking
 * in sequence with 200ms delays between each square, creating a wave effect
 */
@Composable
fun LoadingAnimation() {
    // create infinite animation that repeats
    val transition = rememberInfiniteTransition(label = "loading")

    // base size of squares
    val baseSize = 30.dp

    // growth amount of squares
    val growthAmount = 10.dp

    // animation duration = grow + shrink = (300 + 300)ms
    val duration = 600

    // delay between each square starting
    val delay = 200

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF673AB7)), // set gray background
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // first square
            AnimatedSquare(
                transition = transition,
                baseSize = baseSize, // 30.dp
                growthAmount = growthAmount, // 10.dp
                duration = duration, // 600ms
                delay = 0 // 0ms
            )

            // second square
            AnimatedSquare(
                transition = transition,
                baseSize = baseSize,
                growthAmount = growthAmount,
                duration = duration,
                delay = delay // 200ms
            )

            // third square
            AnimatedSquare(
                transition = transition,
                baseSize = baseSize,
                growthAmount = growthAmount,
                duration = duration,
                delay = delay * 2 // 400ms
            )
        }
    }
}
