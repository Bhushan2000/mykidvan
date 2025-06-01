package com.example.mykidsvan.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mykidsvan.android.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        isVisible = true
        delay(2000) // Show splash for 2 seconds
        navController.navigate("login") {
            popUpTo("splash") {
                inclusive = true
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize()
     ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mkv),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(250.dp)
                )
            }
        }

    }
}