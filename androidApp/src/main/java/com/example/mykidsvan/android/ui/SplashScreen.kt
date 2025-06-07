package com.example.mykidsvan.android.ui

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mykidsvan.android.R
import com.example.mykidsvan.android.utils.LoginState
import com.example.mykidsvan.android.utils.Routes
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

@Composable
fun SplashScreen(
    navController: NavHostController,
    loginState: State<LoginState>
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var checkedInternet by remember { mutableStateOf(false) }

    // 1. Show splash and check internet
    LaunchedEffect(Unit) {
        isVisible = true
        delay(1000)
        val hasInternet = isInternetAvailable(context)

        if (hasInternet) {
            checkedInternet = true
        } else {
            showDialog = true
        }
    }

    // 2. Navigate based on login result
    LaunchedEffect(loginState.value.success, checkedInternet) {
        if (checkedInternet)
            if (loginState.value.success) {
                Toast.makeText(
                    context,
                    loginState.value.message ?: "Welcome back!",
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                // Auto-login failed or not logged in -> go to login
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
    }


    // 3. UI and AlertDialog
    Surface(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("No Internet Connection") },
            text = { Text("Please connect to the internet to continue using the app.") },
            confirmButton = {
                Button(onClick = {
                    activity?.finishAffinity()
                    exitProcess(0)
                }) {
                    Text("Exit App",color = Color.White)
                }
            }
        )
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}