package com.example.mykidsvan.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.screens.AssignedStudentScreen
import com.example.mykidsvan.android.screens.AssignedVehicleScreen
import com.example.mykidsvan.android.screens.DriverSignupScreen
import com.example.mykidsvan.android.screens.FindStudentScreen
import com.example.mykidsvan.android.screens.FindVehicleScreen
import com.example.mykidsvan.android.screens.HomeScreen
import com.example.mykidsvan.android.screens.LoginScreen
import com.example.mykidsvan.android.screens.MessageScreen
import com.example.mykidsvan.android.screens.OTPVerificationScreen
import com.example.mykidsvan.android.screens.ParentSignupScreen
import com.example.mykidsvan.android.screens.PhoneLoginScreen
import com.example.mykidsvan.android.screens.ProfileScreen
import com.example.mykidsvan.android.screens.ReferAppScreen
import com.example.mykidsvan.android.screens.SchoolRegistrationScreen
import com.example.mykidsvan.android.screens.SplashScreen
import com.example.mykidsvan.android.screens.SupportHelpScreen
import com.example.mykidsvan.android.screens.UpdatePasswordScreen
import com.example.mykidsvan.android.utils.DrawerItem
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.getViewModel

class MainActivity : ComponentActivity(), PaymentResultListener {

    // razorpay integration
    private var onPaymentSuccessCallback: (() -> Unit)? = null
    private var onPaymentFailureCallback: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyApp(onPaymentSuccessCallback, onPaymentFailureCallback)
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        onPaymentSuccessCallback?.invoke()
    }

    override fun onPaymentError(code: Int, response: String?) {
         onPaymentFailureCallback?.invoke()
    }


    fun initiateDriverAssignPayment(
        amountInPaise: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        onPaymentSuccessCallback = onSuccess
        onPaymentFailureCallback = onFailure

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_BVJygtmA6ljXBB") // Replace with actual key

        val options = JSONObject().apply {
            put("name", "Assign Vehicle Owner")
            put("description", "Vehicle Owner Request Fee")
            put("currency", "INR")
            put("amount", amountInPaise.toString())

            val prefill = JSONObject().apply {
                put("email", "user@example.com")
                put("contact", "9999999999")
            }
            put("prefill", prefill)
        }
        checkout.open(this, options)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MyApp(onPaymentSuccessCallback: (() -> Unit)?, onPaymentFailureCallback: (() -> Unit)?) {
    val navController = rememberAnimatedNavController() // Use Animated NavController

    val loginViewModel: AuthViewModel = getViewModel()  // Inject ViewModel using Koin
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // user id from prefrences
    val userId by loginViewModel.userId.collectAsState()
    val userRole by loginViewModel.userRole.collectAsState()


    LaunchedEffect(userId) {
        if (userId != null) {
            Log.d("UserID", "Logged-in user ID: $userId")
        }
    }

    // Define which routes should hide the drawer and top bar
    val hideTopBarAndDrawer = currentRoute in listOf(
        "login",
        "phone_login",
        "parent_signup",
        "driver_signup",
        "phone_login",
        "splash",
        "otp_verification/{phone}",
        "update_password/{phone}",
    )

    // Handle navigation with ModalNavigationDrawer and NavHost
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (!hideTopBarAndDrawer) {
                ModalDrawerSheet {
                    Text(
                        text = "My Kids Van",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Dynamically show menus based on userRole
                    val drawerItems = when (userRole?.lowercase()) {
                        "parent" -> listOf(
                            DrawerItem.Home,
                            DrawerItem.Profile,
                            DrawerItem.FindVehicle,
                             DrawerItem.RegisterSchool,
                            DrawerItem.Message,
                            DrawerItem.SupportHelp
                        )

                        "driver" -> listOf(
                            DrawerItem.Home,
                            DrawerItem.FindStudent,
                            DrawerItem.AssignedStudent,
                            DrawerItem.RegisterSchool,
                            DrawerItem.Message,
                            DrawerItem.ReferApp,
                            DrawerItem.SupportHelp
                        )

                        else -> emptyList()
                    }

                    drawerItems.forEach { item ->
                        DrawerItemRow(item) {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        }
                    }
                }
            }
        }

    ) {
        var showLogoutDialog by remember { mutableStateOf(false) }  // Logout dialog state

        Scaffold(
            topBar = {
                if (!hideTopBarAndDrawer) {  // Show top bar only when logged in
                    TopAppBar(
                        title = { Text("My Kid Van") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            // Add logout icon in the top bar
                            IconButton(onClick = {
                                showLogoutDialog = true

                            }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                Modifier.padding(innerPadding)
            ) {
                composable("splash") { SplashScreen(navController) }
                composable("login") { LoginScreen(navController, loginViewModel) }
                composable("otp_verification/{phone}") { backStackEntry ->
                    val phoneNumber = backStackEntry.arguments?.getString("phone") ?: ""
                    OTPVerificationScreen(
                        navController = navController,
                        viewModel = loginViewModel,
                        phoneNumber = phoneNumber
                    )
                }
                composable("parent_signup") { ParentSignupScreen(navController, loginViewModel) }
                composable("driver_signup") { DriverSignupScreen(navController, loginViewModel) }
                composable("phone_login") { PhoneLoginScreen(navController, loginViewModel) }
                composable("update_password/{phone}") { backStackEntry ->
                    val phoneNumber = backStackEntry.arguments?.getString("phone") ?: ""
                    UpdatePasswordScreen(
                        navController = navController,
                        viewModel = loginViewModel,
                        phoneNumber = phoneNumber
                    )
                }
                // Drawer items
                composable(DrawerItem.Home.route) {
                    userId?.let { it1 ->
                        userRole?.let { it2 ->
                            HomeScreen(
                                loginViewModel, "11",
                                it1
                            )
                        }
                    }
                }

                composable(DrawerItem.Profile.route) {
                    userId?.let { id ->
                        userRole?.let { role ->
                            ProfileScreen(loginViewModel, id, role)
                        }
                    }
                }
                composable(DrawerItem.FindStudent.route) { FindStudentScreen(loginViewModel) }
                composable(DrawerItem.AssignedStudent.route) {
                    AssignedStudentScreen(
                        loginViewModel,
                        userId,
                        userRole
                    )
                }
                composable(DrawerItem.RegisterSchool.route) {
                    SchoolRegistrationScreen(
                        loginViewModel
                    )
                }
                composable(DrawerItem.ReferApp.route) { ReferAppScreen() }
                composable(DrawerItem.FindVehicle.route) {
//                    userId?.let { it1 ->
//                        FindVehicleScreen(
//                            loginViewModel,
//                            it1
//                        )
//                    }
                    userId?.let { it1 ->
                        FindVehicleScreen(
                            viewModel = loginViewModel,
                            userId = it1,
                            onPaymentSuccess = { onPaymentSuccessCallback?.invoke() },
                            onPaymentFailure = { onPaymentFailureCallback?.invoke() }
                        )
                    }
                }
                composable(DrawerItem.AssignedVehicle.route) {
                    AssignedVehicleScreen(
                        loginViewModel,
                        userId
                    )
                }
                composable(DrawerItem.Message.route) { MessageScreen() }
                composable(DrawerItem.SupportHelp.route) { SupportHelpScreen() }
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text(text = "Logout") },
                    text = { Text(text = "Are you sure you want to logout?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false

                                // Clear user session or token and reset login state
                                loginViewModel.logout()
                                loginViewModel.resetLoginState()  // Reset the login state to avoid automatic redirection

                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }  // Clear the entire backstack
                                }
                            }
                        ) {
                            Text("Yes", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun DrawerItemRow(item: DrawerItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(item.title, fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreen(navController = rememberNavController())
}
