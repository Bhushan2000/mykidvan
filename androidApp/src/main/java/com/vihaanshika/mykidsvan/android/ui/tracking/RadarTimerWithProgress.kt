package com.vihaanshika.mykidsvan.android.ui.tracking
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RadarTimerWithProgress(timer: Int, isDriverInactive: Boolean) {
    if (!isDriverInactive) {
        val cycleTime = timer % 40

        val targetProgress = when {
            cycleTime <= 10 -> 1f - (cycleTime / 10f)
            else -> 1f - ((cycleTime - 10) / 30f)
        }

        val animatedProgress by animateFloatAsState(
            targetValue = targetProgress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 1000),
            label = "progress"
        )

        val infiniteTransition = rememberInfiniteTransition()
        val radarScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val alpha by animateFloatAsState(
            targetValue = if (timer == 0) 0f else 1f,
            animationSpec = tween(durationMillis = 1000),
            label = "fade"
        )

        val ringColor = getColorForTimer(timer)
        val radarBrush = Brush.radialGradient(
            colors = listOf(ringColor, Color.Transparent),
            radius = 400f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            // 🔵 Radar Pulse
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = radarScale
                        scaleY = radarScale
                        this.alpha = 0.3f * alpha
                    }
                    .background(
                        brush = radarBrush,
                        shape = CircleShape
                    )
            )

            // ⭕ Progress Ring + Time
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha),
                    strokeWidth = 6.dp,
                    color = ringColor
                )

                Text(
                    text = "${cycleTime}s",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}

@Composable
fun getColorForTimer(timer: Int): Color {
    return when {
        timer == 0 -> Color(0xFF00C853) // ✅ Green (Reset)
        timer in 1..10 -> Color(0xFF66BB6A) // ✅ Light Green
        timer in 11..20 -> Color(0xFFFF8A80) // 🔴 Soft Coral Red (Better than pink)
        timer in 21..30 -> Color(0xFFFF5252) // 🔴 Bright Red
        timer in 31..39 -> Color(0xFFC62828) // 🔴 Deep Red
        else -> Color.Red // 🔴 Final fallback (Dark Red)
    }
}
