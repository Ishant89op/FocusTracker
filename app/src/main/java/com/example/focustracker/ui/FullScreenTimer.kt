package com.example.focustracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.focustracker.R
import com.example.focustracker.ui.theme.FocusTrackerTheme
import com.example.focustracker.ui.theme.bodyFontFamily
import com.example.focustracker.ui.theme.displayFontFamily
import kotlinx.coroutines.delay
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.let

@Composable
fun FullScreenTimer(
    focusText: String,
    totalMinutes: Int,
    onExit: () -> Unit,
    onSessionComplete: () -> Unit
) {
    var timeLeftInSeconds by remember { mutableIntStateOf(totalMinutes * 60) }
    var isRunning by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }

    val progress = if (totalMinutes * 60 > 0) {
        (totalMinutes * 60 - timeLeftInSeconds).toFloat() / (totalMinutes * 60)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300), label = ""
    )

    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        activity?.let { act ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                act.window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                val windowInsetsController = WindowCompat.getInsetsController(act.window, act.window.decorView)
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                @Suppress("DEPRECATION")
                act.window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                        )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.let { act ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    act.window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                } else {
                    val windowInsetsController = WindowCompat.getInsetsController(act.window, act.window.decorView)
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

                    @Suppress("DEPRECATION")
                    act.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
    }

    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused && timeLeftInSeconds > 0) {
            delay(1000)
            timeLeftInSeconds--
        }
        if (timeLeftInSeconds == 0) {
            onSessionComplete()
        }
    }

    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF8B5CF6)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F0F23),
            Color(0xFF1A1A2E),
            Color(0xFF16213E)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .windowInsetsPadding(WindowInsets(0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FocusTextSection(
                focusText = focusText,
                modifier = Modifier.weight(0.2f)
            )

            TimerSection(
                timeLeftInSeconds = timeLeftInSeconds,
                totalSeconds = totalMinutes * 60,
                progress = animatedProgress,
                modifier = Modifier.weight(0.6f)
            )

            ControlsSection(
                isRunning = isRunning,
                isPaused = isPaused,
                onPlayPause = { isPaused = !isPaused },
                onStop = {
                    activity?.let { act ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            act.window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                        } else {
                            val windowInsetsController = WindowCompat.getInsetsController(act.window, act.window.decorView)
                            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                        }
                    }
                    onExit()
                },
                modifier = Modifier.weight(0.2f)
            )
        }

        ExitButton(
            onClick = {
                activity?.let { act ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        act.window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    } else {
                        val windowInsetsController = WindowCompat.getInsetsController(act.window, act.window.decorView)
                        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                    }
                }
                onExit()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun FocusTextSection(
    focusText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FOCUSING ON",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            ),
            fontFamily = displayFontFamily,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = focusText.ifEmpty { "Deep Work Session" },
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            fontFamily = bodyFontFamily
        )
    }
}

@Composable
private fun TimerSection(
    timeLeftInSeconds: Int,
    totalSeconds: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                timeLeftInSeconds = timeLeftInSeconds,
                progress = progress,
                modifier = Modifier.fillMaxSize()
            )

            TimerDisplay(
                timeLeftInSeconds = timeLeftInSeconds,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProgressText(
            timeLeftInSeconds = timeLeftInSeconds,
            totalSeconds = totalSeconds
        )
    }
}

@Composable
private fun CircularProgressIndicator(
    timeLeftInSeconds: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 40.dp.toPx()

        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = radius,
            center = center,
            style = Stroke(width = 12.dp.toPx())
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = radius - 20.dp.toPx(),
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        rotate(degrees = -90f, pivot = center) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6),
                        Color(0xFFEC4899)
                    )
                ),
                startAngle = 0f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        val glowRadius = radius + 8.dp.toPx()
        rotate(degrees = -90f, pivot = center) {
            drawArc(
                color = Color(0xFF6366F1).copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - glowRadius, center.y - glowRadius),
                size = Size(glowRadius * 2, glowRadius * 2),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun TimerDisplay(
    timeLeftInSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = timeLeftInSeconds / 60
    val seconds = timeLeftInSeconds % 60

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 50.sp
            ),
            fontFamily = bodyFontFamily
        )

        Text(
            text = "REMAINING",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            ),
            fontFamily = bodyFontFamily
        )
    }
}

@Composable
private fun ProgressText(
    timeLeftInSeconds: Int,
    totalSeconds: Int
) {
    val completedSeconds = totalSeconds - timeLeftInSeconds
    val progressPercentage = if (totalSeconds > 0) {
        (completedSeconds.toFloat() / totalSeconds * 100).toInt()
    } else 0

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$progressPercentage%",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            ),
            fontFamily = bodyFontFamily
        )

        Spacer(modifier = Modifier.width(16.dp))

        LinearProgressIndicator(
            progress = { progressPercentage / 100f },
            modifier = Modifier
                .width(120.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF6366F1),
            trackColor = Color.White.copy(alpha = 0.2f),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

@Composable
private fun ControlsSection(
    isRunning: Boolean,
    isPaused: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            painter = painterResource(R.drawable.close_24px),
            onClick = onStop,
            backgroundColor = Color(0xFFEF4444),
            contentDescription = "Stop session"
        )

        ControlButton(
            painter = if (isPaused) painterResource(R.drawable.play_arrow_24px) else painterResource(R.drawable.pause_24px),
            onClick = onPlayPause,
            backgroundColor = if (isPaused) Color(0xFF10B981) else Color(0xFFF59E0B),
            contentDescription = if (isPaused) "Resume session" else "Pause session"
        )
    }
}

@Composable
private fun ControlButton(
    painter: Painter,
    onClick: () -> Unit,
    backgroundColor: Color,
    contentDescription: String
) {
    Card(
        modifier = Modifier
            .size(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun ExitButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(48.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit fullscreen",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullTimerScreenPreview() {
    FocusTrackerTheme {
        FullScreenTimer(
            focusText = "Focus",
            totalMinutes = 300,
            onExit = {},
            onSessionComplete = {}
        )
    }
}