package com.example.focustracker

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focustracker.data.Screen
import com.example.focustracker.ui.FullScreenTimer
import com.example.focustracker.ui.HomeScreen
import com.example.focustracker.ui.MoodAnalyticsScreen
import com.example.focustracker.ui.theme.FocusTrackerTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusTrackerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    FocusApp()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FocusApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    var isDarkMode by remember { mutableStateOf(false) }
    var currentFocusText by remember { mutableStateOf("") }
    var sessionMinutes by remember { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                isDarkMode = isDarkMode,
                onThemeToggle = {
                    isDarkMode = !isDarkMode
                },
                onMoodAnalytics = {
                    navController.navigate(Screen.Analysis.route)
                },
                onStartFocusSession = { focusText, minutes ->
                    currentFocusText = focusText
                    sessionMinutes = minutes
                    navController.navigate(Screen.Timer.route)
                }
            )
        }

        composable(Screen.Timer.route) {
            FullScreenTimer(
                focusText = currentFocusText,
                totalMinutes = sessionMinutes,
                onExit = {
                    navController.popBackStack()
                },
                onSessionComplete = {
                    // Handle session completion
                    // You can add analytics tracking, notifications, etc.
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Analysis.route) {
            MoodAnalyticsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}