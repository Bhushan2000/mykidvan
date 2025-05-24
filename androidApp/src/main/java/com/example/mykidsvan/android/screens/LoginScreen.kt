package com.example.mykidsvan.android.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val postNotificationPermission = Manifest.permission.POST_NOTIFICATIONS

    val showPermissionRationale = remember { mutableStateOf(false) }
    val showNotificationRationale = remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (!allGranted) {
            showPermissionRationale.value = true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showNotificationRationale.value = true
        }
    }

    LaunchedEffect(Unit) {
        val notGrantedLocation = locationPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedLocation.isNotEmpty()) {
            locationPermissionLauncher.launch(notGrantedLocation.toTypedArray())
        }

        // Notification permission (only Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, postNotificationPermission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(postNotificationPermission)
            }
        }
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (loginState.success && currentRoute == "login") {
        LaunchedEffect(Unit) {
            Toast.makeText(context, loginState.message ?: "Welcome back!", Toast.LENGTH_SHORT)
                .show()
            navController.navigate("home") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    // 📱 SCROLLABLE CONTENT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 👈 Add this line
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Branding or Logo
        Image(
            painter = painterResource(id = R.drawable.mykidvan),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth() // fill horizontally
                .height(150.dp), // set desired height
            contentScale = ContentScale.FillBounds // stretch both horizontally & vertically
        )
        Text(
            text = "Welcome Back!", style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
            ), modifier = Modifier.padding(top = 16.dp)
        )
//        Text(
//            text = "Login to your account",
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//        )

        Spacer(modifier = Modifier.height(32.dp))

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.LightGray,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray
        )

        // Phone Number Input
        OutlinedTextField(
            value = username,
            onValueChange = {
                if (it.length <= 10 && it.all(Char::isDigit)) username = it
            },
            label = { Text("Phone Number") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone, contentDescription = "Phone"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock, contentDescription = "Password"
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable { navController.navigate("phone_login") },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill mandatory fields.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.login(username, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        loginState.error?.let {
            Text(
                text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Social login
        //   Text("Or login with", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        //   SocialLoginButtons()

        Spacer(modifier = Modifier.height(32.dp))

        // Sign up option
        Row {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        showBottomSheet = true
                    }
                },
                fontWeight = FontWeight.Bold
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Signup Type",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please choose your signup option:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            showBottomSheet = false
                            navController.navigate("parent_signup")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Parent Signup", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            showBottomSheet = false
                            navController.navigate("driver_signup")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Driver Signup", color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showPermissionRationale.value) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale.value = false },
            title = { Text("Location Permission Required") },
            text = {
                Text("We need your location to provide accurate pickup and drop-off tracking.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale.value = false
                    locationPermissionLauncher.launch(locationPermissions.toTypedArray())
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionRationale.value = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showNotificationRationale.value) {
        AlertDialog(
            onDismissRequest = { showNotificationRationale.value = false },
            title = { Text("Notification Permission Needed") },
            text = {
                Text("We use notifications to alert you about bus arrivals and updates.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showNotificationRationale.value = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(postNotificationPermission)
                    }
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNotificationRationale.value = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun SocialLoginButtons() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()
    ) {
        SocialSignInButton(
            iconRes = R.drawable.google, text = "Continue with Google", color = Color.White
        )
        SocialSignInButton(
            iconRes = R.drawable.apple, text = "Continue with Apple", color = Color.Black
        )
    }
}

@Composable
fun SocialSignInButton(iconRes: Int, text: String, color: Color) {
    Button(
        onClick = { /* Handle social login */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (color == Color.White) Color.Unspecified else Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (color == Color.White) Color.Black else Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
