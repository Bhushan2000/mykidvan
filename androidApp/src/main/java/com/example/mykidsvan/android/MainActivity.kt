package com.example.mykidsvan.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.mykidsvan.android.screens.LoginScreen
import com.example.mykidsvan.android.screens.MessageScreen
import com.example.mykidsvan.android.screens.OTPVerificationScreen
import com.example.mykidsvan.android.screens.ParentSignupScreen
import com.example.mykidsvan.android.screens.PhoneLoginScreen
import com.example.mykidsvan.android.screens.ProfileScreen
import com.example.mykidsvan.android.screens.ReferAppScreen
import com.example.mykidsvan.android.screens.SchoolOnRegistrationScreen
import com.example.mykidsvan.android.screens.SchoolRegistrationScreen
import com.example.mykidsvan.android.screens.SplashScreen
import com.example.mykidsvan.android.screens.SupportHelpScreen
import com.example.mykidsvan.android.screens.UpdatePasswordScreen
import com.example.mykidsvan.android.screens.VehicleDetailsScreen
import com.example.mykidsvan.android.screens.VehiclePhotoScreen
import com.example.mykidsvan.android.screens.tracking.MapScreen
import com.example.mykidsvan.android.utils.DrawerItem
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.getViewModel


class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    var onPaymentSuccessCallback: ((PaymentData) -> Unit)? = null
    var onPaymentFailureCallback: ((Int, String?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(applicationContext)
        setContent {
            MyApplicationTheme {
                MyApp(onPaymentSuccessCallback, onPaymentFailureCallback)
            }
        }
    }

    fun startPayment(amountInPaise: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_BVJygtmA6ljXBB") // 🔐 Replace with your real key

        val options = JSONObject().apply {
            put("name", "Assign Driver")
            put("description", "Driver Assignment")
            put("currency", "INR")
            put("amount", amountInPaise) // e.g., 100000 = ₹1000

            put("prefill", JSONObject().apply {
                put("email", "example@example.com")
                put("contact", "9876543210")
            })
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Log.d("Razorpay", "Success: $p0")
        p1?.let { onPaymentSuccessCallback?.invoke(it) }
    }

    override fun onPaymentError(code: Int, message: String?, p1: PaymentData?) {
        Log.e("Razorpay", "Error: $message")
        onPaymentFailureCallback?.invoke(code, message)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MyApp(
    onPaymentSuccessCallback: ((PaymentData) -> Unit)?,
    onPaymentFailureCallback: ((Int, String?) -> Unit)?
) {
    val navController = rememberAnimatedNavController() // Use Animated NavController

    val loginViewModel: AuthViewModel = getViewModel()  // Inject ViewModel using Koin
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // user id from prefrences
    val userId by loginViewModel.userId.collectAsState()
    val assignVehicleId by loginViewModel.assignedVehicleId.collectAsState()
    val requestAssignedStatus by loginViewModel.vehicleStatus.collectAsState()
    val userRole by loginViewModel.userRole.collectAsState()


    LaunchedEffect(userId) {
        if (userId != null) {
            Log.d(
                "UserID",
                "Logged-in user ID: $userId & user role - $userRole assigned vehicle id - $assignVehicleId  assign status - $requestAssignedStatus"
            )
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
                            DrawerItem.VehicleDetails,
                            DrawerItem.RegisterSchool,
                            DrawerItem.Message,
                            DrawerItem.SupportHelp
                        )

                        "driver" -> listOf(
                            DrawerItem.Home,
                            DrawerItem.Profile,
                            DrawerItem.FindStudent,
                            DrawerItem.AssignedStudent,
                            DrawerItem.RegisterSchool,
                            DrawerItem.VehiclePhoto,
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
                if (!hideTopBarAndDrawer) {
                    CustomTopAppBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLogoutClick = { showLogoutDialog = true }
                    )
                }
            }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                Modifier.padding(innerPadding)
            ) {
                composable("splash") { SplashScreen(navController) }
                composable("login") { LoginScreen(navController, loginViewModel) }
                composable("schoolOnRegistration") { SchoolOnRegistrationScreen(navController, loginViewModel) }
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
                            assignVehicleId?.let { it3 ->
//                                HomeScreen(
//                                    loginViewModel,
//                                    it1,
//                                    it2,
//                                    it3
//                                )
                            }
                        }
                    }
                    MapScreen()
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
                    userId?.let { it1 ->
                        FindVehicleScreen(
                            viewModel = loginViewModel,
                            userId = it1,
                            onPaymentSuccess = { paymentData ->
                                onPaymentSuccessCallback?.invoke(paymentData)
                            },
                            onPaymentFailure = { code, message ->
                                onPaymentFailureCallback?.invoke(code, message)
                            }
                        )
                    }
                }
                composable(DrawerItem.AssignedVehicle.route) {
                    AssignedVehicleScreen(
                        loginViewModel,
                        userId
                    )
                }
                composable(DrawerItem.VehiclePhoto.route) {
                    userId?.let { it1 ->
                        VehiclePhotoScreen(
                            loginViewModel,
                            it1
                        )
                    }
                }
                composable(DrawerItem.VehicleDetails.route) { VehicleDetailsScreen(loginViewModel,assignVehicleId,userRole) }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

    }
}

@Composable
fun DrawerContent(drawerItems: List<DrawerItem>, onItemClicked: (DrawerItem) -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "My Kids Van",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        drawerItems.forEach { item ->
            DrawerItemRow(item) {
                onItemClicked(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String = "My Kid Van",
    onMenuClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onLogoutClick) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    )
}


@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreen(navController = rememberNavController())
}
