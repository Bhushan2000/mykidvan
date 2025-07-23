package com.vihaanshika.mykidsvan.android

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.maptracking.LatLngViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.vihaanshika.mykidsvan.android.data.MessagesViewModel
import com.vihaanshika.mykidsvan.android.ui.AboutDeveloperScreen
import com.vihaanshika.mykidsvan.android.ui.AssignedStudentScreen
import com.vihaanshika.mykidsvan.android.ui.ChatScreen
import com.vihaanshika.mykidsvan.android.ui.DriverSignupScreen
import com.vihaanshika.mykidsvan.android.ui.FindStudentScreen
import com.vihaanshika.mykidsvan.android.ui.FindVehicleScreen
import com.vihaanshika.mykidsvan.android.ui.LoginScreen
import com.vihaanshika.mykidsvan.android.ui.MessageScreen
import com.vihaanshika.mykidsvan.android.ui.OTPVerificationScreen
import com.vihaanshika.mykidsvan.android.ui.ParentSignupScreen
import com.vihaanshika.mykidsvan.android.ui.PhoneLoginScreen
import com.vihaanshika.mykidsvan.android.ui.ProfileScreen
import com.vihaanshika.mykidsvan.android.ui.ReferAppScreen
import com.vihaanshika.mykidsvan.android.ui.RemoteImageWithProgress
import com.vihaanshika.mykidsvan.android.ui.SchoolOnRegistrationScreen
import com.vihaanshika.mykidsvan.android.ui.SchoolRegistrationScreen
import com.vihaanshika.mykidsvan.android.ui.SplashScreen
import com.vihaanshika.mykidsvan.android.ui.SupportHelpScreen
import com.vihaanshika.mykidsvan.android.ui.UpdatePasswordScreen
import com.vihaanshika.mykidsvan.android.ui.UploadDocumentsScreen
import com.vihaanshika.mykidsvan.android.ui.VehicleDetailsScreen
import com.vihaanshika.mykidsvan.android.ui.VehiclePhotoScreen
import com.vihaanshika.mykidsvan.android.ui.WithDrawRequests
import com.vihaanshika.mykidsvan.android.ui.tracking.MapScreen
import com.vihaanshika.mykidsvan.android.utils.Arguments
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.DrawerItem
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.vihaanshika.mykidsvan.android.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    var onPaymentSuccessCallback: ((PaymentData) -> Unit)? = null
    var onPaymentFailureCallback: ((Int, String?) -> Unit)? = null
    val loginViewModel: AuthViewModel by viewModel()

    // app updater
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val REQUEST_CODE = 777
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(applicationContext)
        checkForUpdate()
        setContent {
            MyApplicationTheme {
                // Capture intent data
                val openMessageScreen = intent?.getBooleanExtra("openMessageScreen", false) == true
                if (openMessageScreen) {
                    loginViewModel.triggerMessageScreenNavigation()
                }
                MyApp(onPaymentSuccessCallback, onPaymentFailureCallback)
            }
        }
    }

    fun startPayment(amountInPaise: Int?, orderId: String?) {
        val checkout = Checkout()
//        checkout.setKeyID(Constants.RAZORPAY_TEST_KEY) // 🔐 Replace with your real key
        checkout.setKeyID(BuildConfig.RAZORPAY_ID) // 🔐 Replace with your real key
        val options = JSONObject().apply {
            put("name", Constants.PAYMENT_NAME)
            put("description", Constants.PAYMENT_DESCRIPTION)
            put("currency", Constants.PAYMENT_CURRENCY)
            put("amount", amountInPaise) // e.g., 100000 = ₹1000
            put("order_id", orderId) // 🔐 Add order ID received from server here

            put("retry", JSONObject().apply {
                put("enabled", true)
                put("max_count", 4)
            })

            put("prefill", JSONObject().apply {
                put("email", Constants.PREFILL_EMAIL)
                put("contact", Constants.PREFILL_CONTACT)
            })
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, p1: PaymentData?) {
        Log.d("Razorpay", "onPaymentSuccess: PaymentId $razorpayPaymentId")
        Log.d("Razorpay", "onPaymentSuccess: OrderID ${p1?.orderId}")
        Log.d("Razorpay", "onPaymentSuccess: Signature ${p1?.signature}")
        p1?.let { onPaymentSuccessCallback?.invoke(it) }
    }

    override fun onPaymentError(code: Int, message: String?, p1: PaymentData?) {
        Log.e("Razorpay", "Error: $message")
        onPaymentFailureCallback?.invoke(code, message)
    }

    private fun checkForUpdate() {
//        Types of In-App Updates:
//        Flexible update – App can be used while downloading update.
//        Immediate update – App is blocked until update is completed.

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d("AppUpdate", "Update Availability: ${appUpdateInfo.updateAvailability()}")
            Log.d(
                "AppUpdate",
                "Is Immediate Update Allowed: ${appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)}"
            )
            Log.d("AppUpdate", "Available Version Code: ${appUpdateInfo.availableVersionCode()}")
            if (
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                // For IMMEDIATE
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    this,
                    REQUEST_CODE
                )
            }

            // For FLEXIBLE update, use this instead:
            /*
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    FLEXIBLE,
                    this,
                    REQUEST_CODE
                )

                appUpdateManager.registerListener(installStateUpdatedListener)
            }
            */
        }
    }

    // Optional: Listen to FLEXIBLE install completion
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(this, "Update downloaded. Restarting...", Toast.LENGTH_LONG).show()
            appUpdateManager.completeUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if update was downloaded but not installed
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MyApp(
    onPaymentSuccessCallback: ((PaymentData) -> Unit)?,
    onPaymentFailureCallback: ((Int, String?) -> Unit)?,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val loginViewModel: AuthViewModel = getViewModel()  // Inject ViewModel using Koin
    val messagesViewModel: MessagesViewModel = getViewModel()
    val latViewModel: LatLngViewModel = getViewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isMapScreen = currentRoute == Constants.ROUTE_HOME

    // user id from preferences
    val userId by loginViewModel.userId.collectAsState()
    val assignVehicleId by loginViewModel.assignedVehicleId.collectAsState()
    val requestAssignedStatus by loginViewModel.vehicleStatus.collectAsState()
    val userRole by loginViewModel.userRole.collectAsState()
    val trialDate by loginViewModel.trialDate.collectAsState()
    val paymentStatus by loginViewModel.paymentStatus.collectAsState()
    val schoolPictureUrl by loginViewModel.schoolPictureUrl.collectAsState()
    val referCode by loginViewModel.referCode.collectAsState()
    val payAmount by loginViewModel.payAmount.collectAsState()

    var selectedMenuTitle by remember { mutableStateOf(Constants.MY_KID_VAN) } // Initial title
    var selectedDrawerItem by remember { mutableStateOf<DrawerItem?>(null) }

    // Collect your flag
    val openMessageScreen by loginViewModel.navigateToMessageScreen.collectAsState()
    var refreshKey by remember { mutableStateOf(UUID.randomUUID().toString()) }
    val coroutineScope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null && userRole.equals(Constants.USER_PARENT)) {
            Log.d(
                "UserDetails :) ",
                "user ID - $userId \n" +
                        "user role - $userRole  \n" +
                        "trialDate - $trialDate  \n" +
                        "schoolPictureUrl - $schoolPictureUrl  \n" +
                        "refer code - $referCode  \n" +
                        "current route - $currentRoute \n" +
                        "payment status - $paymentStatus  \n" +
                        "assigned vehicle id - $assignVehicleId   \n" +
                        "assign status - $requestAssignedStatus   \n" +
                        "payAmount - $payAmount \n"
            )
        } else if (userId != null && userRole.equals(Constants.USER_DRIVER)) {

            Log.d(
                "UserDetails :) ",
                "user ID - $userId \n" +
                        "user role - $userRole  \n" +
                        "trialDate - $trialDate  \n" +
                        "schoolPictureUrl - $schoolPictureUrl  \n" +
                        "refer code - $referCode  \n" +
                        "current route - $currentRoute \n" +
                        "payAmount - $payAmount \n"

            )
        }
    }

    LaunchedEffect(currentRoute) {
        selectedDrawerItem = when (currentRoute) {
            DrawerItem.Home.route -> DrawerItem.Home
            DrawerItem.Profile.route -> DrawerItem.Profile
            DrawerItem.FindVehicle.route -> DrawerItem.FindVehicle
            DrawerItem.VehicleDetails.route -> DrawerItem.VehicleDetails
            DrawerItem.Message.route -> DrawerItem.Message
            DrawerItem.Commission.route -> DrawerItem.Commission
            DrawerItem.ReferApp.route -> DrawerItem.ReferApp
            DrawerItem.SupportHelp.route -> DrawerItem.SupportHelp
            DrawerItem.AboutDeveloper.route -> DrawerItem.AboutDeveloper
            DrawerItem.FindStudent.route -> DrawerItem.FindStudent
            DrawerItem.AssignedStudent.route -> DrawerItem.AssignedStudent
            DrawerItem.VehiclePhoto.route -> DrawerItem.VehiclePhoto
            else -> null
        }
        selectedMenuTitle = selectedDrawerItem?.title ?: Constants.MY_KID_VAN
    }


    // Navigate only once after launch if needed
    LaunchedEffect(openMessageScreen) {
        if (openMessageScreen) {
            navController.navigate(Constants.ROUTE_MESSAGES) {
                popUpTo("home") { inclusive = true } // or splash/home
                launchSingleTop = true
            }
            loginViewModel.consumeNavigationFlag()
        }
    }

    // Define which routes should hide the drawer and top bar
    val hideTopBarAndDrawer = currentRoute in listOf(
        Routes.LOGIN,
        Routes.PHONE_LOGIN,
        Routes.PARENT_SIGNUP,
        Routes.DRIVER_SIGNUP,
        Routes.SPLASH,
        Routes.OTP_VERIFICATION,
        Routes.UPDATE_PASSWORD,
        Routes.SCHOOL_ON_REGISTRATION,
        Routes.FILE_UPLOAD
    )

    fun onDrawerItemClick(route: String) {
        coroutineScope.launch {
            drawerState.close()
            if (navController.currentDestination?.route != route) {
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            }
        }
    }

    // Handle navigation with ModalNavigationDrawer and NavHost
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isMapScreen,
        drawerContent = {
            if (!hideTopBarAndDrawer) {
                ModalDrawerSheet {
                    Text(
                        text = Constants.MY_KID_VAN,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Dynamically show menus based on userRole
                    val drawerItems = when (userRole?.lowercase()) {
                        Constants.USER_PARENT -> listOf(
                            DrawerItem.Home,
                            DrawerItem.Profile,
                            DrawerItem.FindVehicle,
                            DrawerItem.VehicleDetails,
                            DrawerItem.Message,
                            DrawerItem.Commission,
                            DrawerItem.ReferApp,
                            DrawerItem.SupportHelp,
                            DrawerItem.AboutDeveloper
                        )

                        Constants.USER_DRIVER -> listOf(
                            DrawerItem.Home,
                            DrawerItem.Profile,
                            DrawerItem.FindStudent,
                            DrawerItem.AssignedStudent,
                            DrawerItem.VehiclePhoto,
                            DrawerItem.Message,
                            DrawerItem.Commission,
                            DrawerItem.ReferApp,
                            DrawerItem.SupportHelp,
                            DrawerItem.AboutDeveloper
                        )

                        else -> emptyList()
                    }

                    drawerItems.forEach { item ->
                        DrawerItemRow(
                            item = item,
                            isSelected = item == selectedDrawerItem,
                            onClick = {
                                selectedDrawerItem = item
                                selectedMenuTitle = item.title
                                onDrawerItemClick(item.route) // ✅ delegate navigation
                            }
                        )
                    }

                }
            }
        }
    ) {
        var showLogoutDialog by remember { mutableStateOf(false) }  // Logout dialog state
        val ultraSmoothFadeSpec = tween<Float>(
            durationMillis = 1000,
            easing = FastOutSlowInEasing // smoother than LinearOutSlowInEasing
        )

        val ultraSmoothScaleSpec = tween<Float>(
            durationMillis = 1000,
            easing = CubicBezierEasing(0.2f, 0f, 0f, 1f) // soft spring-like smoothness
        )
        Scaffold(
            topBar = {
                if (!hideTopBarAndDrawer) {
                    CustomTopAppBar(
                        title = selectedMenuTitle,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLogoutClick = { showLogoutDialog = true },
                        schoolPictureUrl = schoolPictureUrl
                    )
                }
            },
            content = { innerPadding ->
                // Add extra top padding to shift the content below the elevated app bar
                val contentPadding = PaddingValues(
//                    top = innerPadding.calculateTopPadding() + 72.dp, // Adjust this as needed
                    top = 0.dp, // Adjust this as needed
                    start = 0.dp,
                    end = 0.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
                AnimatedNavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH,
                    modifier = Modifier.padding(contentPadding),

                    enterTransition = {
                        fadeIn(animationSpec = ultraSmoothFadeSpec) +
                                scaleIn(
                                    animationSpec = ultraSmoothScaleSpec,
                                    initialScale = 0.98f // smaller zoom-in effect
                                )
                    },

                    exitTransition = {
                        fadeOut(animationSpec = ultraSmoothFadeSpec) +
                                scaleOut(
                                    animationSpec = ultraSmoothScaleSpec,
                                    targetScale = 1.01f // softer zoom-out
                                )
                    },

                    popEnterTransition = {
                        fadeIn(animationSpec = ultraSmoothFadeSpec) +
                                scaleIn(
                                    animationSpec = ultraSmoothScaleSpec,
                                    initialScale = 0.98f
                                )
                    },

                    popExitTransition = {
                        fadeOut(animationSpec = ultraSmoothFadeSpec) +
                                scaleOut(
                                    animationSpec = ultraSmoothScaleSpec,
                                    targetScale = 1.01f
                                )
                    }


                ) {
                    composable(Routes.SPLASH) {
                        SplashScreen(
                            navController,
                            loginViewModel.loginState.collectAsState()
                        )
                    }
                    composable(Routes.LOGIN) { LoginScreen(navController, loginViewModel) }
                    composable(
                        route = Routes.SCHOOL_ON_REGISTRATION,
                        arguments = listOf(
                            navArgument(Arguments.UID) { type = NavType.StringType },
                            navArgument(Arguments.ROLE) { type = NavType.StringType }
                        )) { navBackStackEntry ->
                        val uid = navBackStackEntry.arguments?.getString(Arguments.UID) ?: ""
                        val role = navBackStackEntry.arguments?.getString(Arguments.ROLE)

                        SchoolOnRegistrationScreen(
                            navController,
                            loginViewModel,
                            uid,
                            role
                        )
                    }
                    composable(Routes.FILE_UPLOAD) { backStackEntry ->
                        val uid = backStackEntry.arguments?.getString(Arguments.UID) ?: ""
                        UploadDocumentsScreen(navController, loginViewModel, uid) { base64Map ->
                            loginViewModel.uploadDocumentsToDatabase(base64Map)
                        }
                    }

                    composable(Routes.OTP_VERIFICATION) { backStackEntry ->
                        val phoneNumber = backStackEntry.arguments?.getString(Arguments.PHONE) ?: ""
                        OTPVerificationScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            phoneNumber = phoneNumber
                        )
                    }
                    composable(Routes.PARENT_SIGNUP) {
                        ParentSignupScreen(
                            navController,
                            loginViewModel
                        )
                    }
                    composable(Routes.DRIVER_SIGNUP) {
                        DriverSignupScreen(
                            navController,
                            loginViewModel
                        )
                    }
                    composable(Routes.PHONE_LOGIN) {
                        PhoneLoginScreen(
                            navController,
                            loginViewModel
                        )
                    }
                    composable(Routes.UPDATE_PASSWORD) { backStackEntry ->
                        val phoneNumber = backStackEntry.arguments?.getString(Arguments.PHONE) ?: ""
                        UpdatePasswordScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            phoneNumber = phoneNumber
                        )
                    }
                    // Drawer items
                    composable(DrawerItem.Home.route) {
                        key(refreshKey) {
                            MapScreen(
                                drawerState = drawerState,
                                paymentStatus = paymentStatus,
                                requestAssignedStatus = requestAssignedStatus,
                                viewModel = latViewModel,
                                authViewModel = loginViewModel,
                                userRole = userRole.toString(),
                                userId = userId.toString(),
                                onRefresh = {
                                    refreshKey = UUID.randomUUID().toString()
                                }, // 🔁 Refresh handler
                                trialDate = trialDate,
                                onPaymentSuccess = { paymentData ->
                                    onPaymentSuccessCallback?.invoke(paymentData)
                                },
                                onPaymentFailure = { code, message ->
                                    onPaymentFailureCallback?.invoke(code, message)
                                }
                            )
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
                    composable(DrawerItem.ReferApp.route) {
                        ReferAppScreen(
                            loginViewModel,
                            userRole.toString(),
                            userId.toString()
                        )
                    }
                    composable(DrawerItem.FindVehicle.route) {
                        userId?.let { it1 ->
                            FindVehicleScreen(
                                requestAssignedStatus = requestAssignedStatus,
                                assignVehicleId = assignVehicleId,
                                viewModel = loginViewModel,
                                userId = it1,
                                trialDate = trialDate,
                                paymentStatus = paymentStatus,
                                onPaymentSuccess = { paymentData ->
                                    onPaymentSuccessCallback?.invoke(paymentData)
                                },
                                onPaymentFailure = { code, message ->
                                    onPaymentFailureCallback?.invoke(code, message)
                                },
                                payAmount = payAmount
                            )
                        }
                    }

                    composable(DrawerItem.VehiclePhoto.route) {
                        userId?.let { it1 ->
                            VehiclePhotoScreen(
                                loginViewModel,
                                it1
                            )
                        }
                    }
                    composable(DrawerItem.VehicleDetails.route) {
                        VehicleDetailsScreen(
                            loginViewModel,
                            assignVehicleId,
                            requestAssignedStatus,
                            userRole
                        )
                    }
                    composable(DrawerItem.Message.route) {
                        MessageScreen(
                            navController,
                            messagesViewModel,
                            userId,
                            userRole,
                            assignVehicleId,
                            requestAssignedStatus
                        )
                    }
                    composable(Routes.CHAT) { backStackEntry ->
                        val chatTarget = backStackEntry.arguments?.getString(Arguments.CHAT_TYPE)

                        ChatScreen(
                            parentIdOrName = if (chatTarget == Constants.GROUP_CHAT) null else chatTarget,
                            viewModel = messagesViewModel,
                            navController = navController,
                            assignVehicleId = assignVehicleId,
                            userId = userId,
                            userRole = userRole
                        )
                    }
                    composable(DrawerItem.SupportHelp.route) { SupportHelpScreen() }
                    composable(DrawerItem.AboutDeveloper.route) { AboutDeveloperScreen() }
                    composable(DrawerItem.Commission.route) {
                        WithDrawRequests(
                            loginViewModel,
                            userId.toString(),
                            userRole.toString()
                        )
                    }
                }
            }
        )

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = PlaceHolders.LOGOUT) },
                text = { Text(text = PlaceHolders.ARE_YOU_SURE_YOU_WANT_TO_LOGOUT) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false

                            // Reset the selected drawer item
                            selectedDrawerItem =
                                null // or drawerItems.first() if you want a default

                            // Optional: reset menu title too
                            selectedMenuTitle = Constants.MY_KID_VAN // or any default

                            loginViewModel.resetLoginState()  // Reset the login state to avoid automatic redirection
                            // Clear user session or token and reset login state
                            loginViewModel.logout()
                            latViewModel.stopTracking() // stop tracking
                            if (userRole == Constants.USER_PARENT) {
                                latViewModel.updateParentActiveInactiveStatus(
                                    userId.toString(),
                                    Constants.INACTIVE_PARENT
                                )
                                Log.d(
                                    "ParentActiveInactive MainActivity On Logout",
                                    "Parent Status - Updated to InActive"
                                )
                            }
                            // Add slight delay to ensure state flows are reset
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100) // 100ms
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text(Constants.YES, color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(Constants.No)
                    }
                }
            )
        }
    }

    BackHandler {
        coroutineScope.launch {
            when {
                drawerState.isOpen -> {
                    drawerState.close()
                }

                currentRoute != DrawerItem.Home.route -> { // or Constants.ROUTE_HOME
                    navController.navigate(DrawerItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                else -> {
                    showExitDialog = true
                }
            }
        }
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit App") },
            text = { Text("Are you sure you want to exit the app?") },
            confirmButton = {
                TextButton(onClick = {
                    latViewModel.stopTracking()
                    if (userRole == Constants.USER_DRIVER) {
                        latViewModel.stopDriverTracking(
                            userId.toString(),
                            Constants.INACTIVE_TRACKING
                        )
                    } else if (userRole == Constants.USER_PARENT) {
                        latViewModel.updateParentActiveInactiveStatus(
                            userId.toString(),
                            Constants.INACTIVE_PARENT
                        )
                        Log.d(
                            "ParentActiveInactive MainActivity",
                            "Parent Status - Updated to InActive"
                        )
                    }
                    showExitDialog = false
                    // Exit the screen (use NavController if you're using Navigation)
                    (context as? Activity)?.finish()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

}


@Composable
fun DrawerItemRow(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp) // Outer padding around the highlight shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape = RoundedCornerShape(32.dp)) // Rounded ends
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp) // Inner content padding
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = item.title,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                color = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 18.sp
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onMenuClick: () -> Unit,
    onLogoutClick: () -> Unit,
    schoolPictureUrl: String?
) {
    val topBarPadding = 12.dp
    val horizontalPadding = 12.dp
    val appBarHeight = 68.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(appBarHeight)
            .padding(
                top = topBarPadding,
                start = horizontalPadding,
                end = horizontalPadding
            )
            .shadow(
                elevation = 40.dp,
                shape = RoundedCornerShape(48.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                shape = RoundedCornerShape(48.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(48.dp)
            )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = PlaceHolders.MENU,
                        modifier = Modifier.padding(start = 8.dp),
                        tint = Color.Unspecified
                    )
                }
            },
            actions = {
                RemoteImageWithProgress(schoolPictureUrl)
                IconButton(onClick = onLogoutClick) {
                    Icon(Icons.Default.ExitToApp, contentDescription = PlaceHolders.LOGOUT)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}