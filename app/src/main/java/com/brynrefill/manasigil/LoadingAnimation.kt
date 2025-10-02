package com.brynrefill.manasigil

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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

/**
 * a single animated square that grows and shrinks
 *
 * @param transition - infinite transition for the animation
 * @param baseSize - base size of the square
 * @param growthAmount - how much the square should grow
 * @param duration - animation duration of the grow + shrink cycle
 * @param delay - delay before this square starts animating
 */
@Composable
fun AnimatedSquare(
    transition: InfiniteTransition,
    baseSize: Dp,
    growthAmount: Dp,
    duration: Int,
    delay: Int
) {
    // animate the size of the square
    // that goes from baseSize to (baseSize + growthAmount) and back
    val size by transition.animateFloat(
        initialValue = baseSize.value,
        targetValue = baseSize.value + growthAmount.value,
        // define how the animation behaves
        animationSpec = infiniteRepeatable( // tell Compose to repeat this animation forever
            animation = keyframes { // define specific keyframes (points in time) for the animation (like a timeline)
                // duration cycle of the third square.
                // The animation terminates when the third square animation terminates
                durationMillis = duration + 400
                /*
                square 1: 600 +   0 =  600ms
                square 2: 600 + 200 =  800ms
                square 3: 600 + 400 = 1000ms (=durationMillis)
                */

                // "the size should be X, at time Y"
                // stay at base size during delay
                // easing controls the speed/acceleration of the animation over time.
                // LinearEasing specifically means constant speed throughout the animation
                baseSize.value at delay using LinearEasing // TODO: check for EaseInOutBack, seems interesting
                /*
                square 1: size = 30dp (base size),             at   0ms (=delay)
                square 2: size = 30dp (stays small for 200ms), at 200ms
                square 3: size = 30dp (stays small for 400ms), at 400ms
                */

                // grow to max size
                (baseSize.value + growthAmount.value) at (duration / 2 + delay) using LinearEasing
                /* grown by 10dp
                square 1: size = 40dp, at 300ms (300 +   0)
                square 2: size = 40dp, at 500ms (300 + 200)
                square 3: size = 40dp, at 700ms (300 + 400)
                */

                // shrink back to base size
                baseSize.value at (duration + delay) using LinearEasing
                /*
                square 1: size = 30dp, at  600ms (600 +   0)
                square 2: size = 30dp, at  800ms (600 + 200)
                square 3: size = 30dp, at 1000ms (600 + 400)
                */

                // stay at base size for remaining time
                baseSize.value at (durationMillis) using LinearEasing
                /* end of cycle
                square 1: size = 30dp, at 1000ms (= durationMillis)
                square 2: size = 30dp, at 1000ms
                square 3: size = 30dp, at 1000ms
                */
            },
            repeatMode = RepeatMode.Restart // after finishing one cycle, restart from the beginning (not reverse)
        ),
        label = "square_size"
    )

    // animate the color of the square
    // that changes from gray to white when growing, back to gray when shrinking
    val color by transition.animateColor(
        initialValue = Color(0xFF373434), // set gray color
        targetValue = Color.White,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration + 400

                // stay gray during delay
                Color(0xFF424242) at delay using LinearEasing

                // change to white when at max size
                Color.White at (duration / 2 + delay) using LinearEasing

                // change back to dark gray when shrunk
                Color(0xFF424242) at (duration + delay) using LinearEasing

                // stay dark gray for remaining time
                Color(0xFF424242) at (durationMillis) using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "square_color"
    )

    // draw the square
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color) // set gray/white background
    )
}