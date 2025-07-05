package com.vihaanshika.mykidsvan.android.ui.tracking

import com.google.maps.android.compose.Polyline
import com.google.android.gms.maps.model.JointType
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*

import kotlinx.coroutines.launch

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.SystemClock
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.maptracking.LatLngViewModel
import com.vihaanshika.mykidsvan.android.R
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.razorpay.PaymentData
import com.vihaanshika.mykidsvan.android.MainActivity
import com.vihaanshika.mykidsvan.android.utils.LocationFetcher
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable

fun MapScreen(
    paymentStatus: String?,
    requestAssignedStatus: String?,
    viewModel: LatLngViewModel = koinViewModel(),
    authViewModel: AuthViewModel,
    userRole: String,
    userId: String,
    onRefresh: () -> Unit,
    trialDate: String?,
    onPaymentSuccess: (PaymentData) -> Unit,
    onPaymentFailure: (Int, String?) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val coroutineScope = rememberCoroutineScope()
    val parentLatLngList by viewModel.latLngList.collectAsState()
    val driverLatLngList by viewModel.localLatLngList.collectAsState()
    val latLngList = if (userRole == Constants.USER_PARENT) {
        parentLatLngList
    } else {
        driverLatLngList
    }

    val bearing by viewModel.bearing.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    val userRole by viewModel.userRole.collectAsState()
    val assignedVehicleId by viewModel.assignedVehicleId.collectAsState()
    val vehicleTrackingStatus by viewModel.vehicleTrackingStatus.collectAsState()
    // counter
    val timer = remember { mutableStateOf(0) }
    // Track changes to last LatLng
    var lastKnownPoint by remember { mutableStateOf(latLngList.lastOrNull()) }
    val fullPolylineListDriver by viewModel.visiblePolylinePath.collectAsState()
    val fullPolylineListParent by viewModel.visiblePolylinePathParent.collectAsState()
    val fullPolylineList = if (userRole == Constants.USER_PARENT) {
        fullPolylineListParent
    } else {
        fullPolylineListDriver
    }

    var showExitDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val today = dateFormat.parse(dateFormat.format(Date()))
    val trial = if (!trialDate.isNullOrBlank()) {
        dateFormat.parse(trialDate)
    } else {
        Log.e("MapScreen", "trialDate is null or blank!")
        null
    }

    // payments
    var showPaymentDialog by remember { mutableStateOf(false) }
    var redirectToPayment by remember { mutableStateOf(false) }
    val hasTriggeredPaymentDialog = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showSheet = remember { mutableStateOf(false) }
    val currentDate = LocalDate.now()
    val paymentDate = currentDate.format(DateTimeFormatter.ISO_DATE)
    val expireDate = currentDate.plusYears(1).format(DateTimeFormatter.ISO_DATE)

    val orderIdState by authViewModel.getOrderId.collectAsState()
    var razorOrderId by remember { mutableStateOf<String?>(null) }
    var razorAmount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(orderIdState) {
        if (orderIdState is Resource.Success) {
            val data = (orderIdState as Resource.Success).data
            data.let {
                razorOrderId = it.orderId
                razorAmount = it.amountPaise
            }
        }
    }

    // Razorpay Callbacks Setup
    LaunchedEffect(Unit) {
        activity?.onPaymentSuccessCallback = { paymentData ->
            onPaymentSuccess(paymentData)
            authViewModel.updatePaymentStatus(
                id = userId.toInt(),
                paymentId = paymentData.paymentId ?: "TXN",
                amount = (razorAmount?.div(100)).toString(),
                paymentStatus = "Paid",
                expireDate = expireDate,
                paymentDate = paymentDate,
                assignStatus = "Assigned",
                assignDate = paymentDate,
                signature = paymentData.signature,
                orderId = paymentData.orderId
            )
        }

        activity?.onPaymentFailureCallback = { code, message ->
            Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "MapScreen: $message")
            onPaymentFailure(code, message)
        }
    }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            viewModel.registerLocationReceiverIfNeeded(context, userRole.toString())
        } else {
            viewModel.unregisterLocationReceiverIfNeeded(context, userRole.toString())
        }
    }

    /*     this destroy service when page is leave
        DisposableEffect(userRole) {
            onDispose {
                viewModel.stopTracking() // Stop LiveData/StateFlow updates
            }
        }*/

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().target(latLng).zoom(20f).tilt(45f).bearing(0f)
                                .build()
                        ), durationMs = 1000
                    )
                    viewModel.startTracking()
                }
            }
        } else {
            Toast.makeText(context, PlaceHolders.GPS_REQUIRED_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndPromptEnableGps() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            coroutineScope.launch {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().target(latLng).zoom(20f).tilt(45f).bearing(0f)
                                .build()
                        ), durationMs = 1000
                    )
                    /////////////// start the service  //////////////////////////////
                    viewModel.startTrackingService(context)
                    viewModel.startTracking()

                }
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(
                        context,
                        PlaceHolders.MSG_UNABLE_TO_REQUEST_GPS,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(context, PlaceHolders.MSG_ENABLE_GPS_MANUALLY, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    // 🔴 Track if the driver is inactive for 40 seconds
    val isDriverInactive = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(latLngList.lastOrNull()) {
        if (userRole == Constants.USER_PARENT) {
            val currentPoint = latLngList.lastOrNull()
            if (currentPoint != lastKnownPoint) {
                // Movement detected — reset timer and inactivity flag
                timer.value = 0
                isDriverInactive.value = false
                lastKnownPoint = currentPoint
            }
        }
    }

    // Timer loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (userRole == Constants.USER_PARENT) {
                if (timer.value < 40) {
                    timer.value++
                } else {
                    isDriverInactive.value = true
                }
            }
        }
    }


    // 🔁 Auto-start/stop tracking for parent based on assignedVehicleId and status
    LaunchedEffect(userRole, assignedVehicleId, vehicleTrackingStatus) {
        if (userRole == Constants.USER_PARENT) {
            if (!assignedVehicleId.isNullOrBlank() && vehicleTrackingStatus.equals(
                    PlaceHolders.ACCEPTED, ignoreCase = true
                )
            ) {
                if (!isTracking) {
                    viewModel.startTrackingService(context)
                    viewModel.startTracking()
                }
            } else {
                if (isTracking) {
                    viewModel.stopTracking()
                }
            }
        }
    }

    // Request location permission on first launch (only for driver)
    LaunchedEffect(Unit) {
        if (userRole == Constants.USER_DRIVER) {
            if (!locationPermissionState.status.isGranted) {
                locationPermissionState.launchPermissionRequest()
            } else {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().target(latLng).zoom(20f).tilt(45f).bearing(0f)
                                .build()
                        ), durationMs = 1000
                    )
                }
            }
        }
    }

    // Follow the latest location update to animate camera
    LaunchedEffect(latLngList.lastOrNull()) {
        latLngList.lastOrNull()?.let { newLatLng ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(newLatLng, 18f),
                1000
            )
        }
    }

    // for dark mode of the map

    val darkMapStyleJson = """
        [
          {
            "elementType": "geometry",
            "stylers": [{"color": "#0f1a30"}]   // Darker background geometry
          },
          {
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#a0c4c8"}]  // Slightly brighter text fill
          },
          {
            "elementType": "labels.text.stroke",
            "stylers": [{"color": "#0b1a2b"}]  // Darker text stroke for better contrast
          },
          {
            "featureType": "administrative.country",
            "elementType": "geometry.stroke",
            "stylers": [{"color": "#3b5360"}]  // Slightly darker boundary lines
          },
          {
            "featureType": "administrative.land_parcel",
            "stylers": [{"visibility": "off"}]
          },
          {
            "featureType": "landscape.man_made",
            "elementType": "geometry.stroke",
            "stylers": [{"color": "#25406d"}]  // Deeper color for man-made structures
          },
          {
            "featureType": "poi",
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#5f8a96"}]  // More muted poi label color
          },
          {
            "featureType": "poi.park",
            "elementType": "geometry.fill",
            "stylers": [{"color": "#014455"}]  // Darker park fill
          },
          {
            "featureType": "poi.park",
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#2c5f6a"}]  // Darker park label text
          },
          {
            "featureType": "road",
            "elementType": "geometry",
            "stylers": [{"color": "#223d6f"}]  // Dark blue roads
          },
          {
            "featureType": "road",
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#a5b4cc"}]  // Slightly brighter road text
          },
          {
            "featureType": "road",
            "elementType": "labels.text.stroke",
            "stylers": [{"color": "#0f1a30"}]  // Darker road text stroke
          },
          {
            "featureType": "transit",
            "elementType": "geometry",
            "stylers": [{"color": "#23364b"}]  // Deeper transit geometry color
          },
          {
            "featureType": "transit.station",
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#d07b43"}]  // Slightly warmer transit station labels
          },
          {
            "featureType": "water",
            "elementType": "geometry",
            "stylers": [{"color": "#07141f"}]  // Darker water color
          },
          {
            "featureType": "water",
            "elementType": "labels.text.fill",
            "stylers": [{"color": "#3d5a5f"}]  // Muted water labels
          }
        ]
        """.trimIndent()

    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleOptions = remember {
        if (isDarkTheme) {
            MapStyleOptions(darkMapStyleJson)
        } else null // Default Google Map Light style
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, floatingActionButton = {
            if (userRole == Constants.USER_DRIVER)
                CurrentLocationFab(
                    viewModel = viewModel,
                    cameraPositionState = cameraPositionState,
                    currentLocation = currentLocation,
                    context = context
                )
        }
    ) { paddingValue ->
        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted && shouldShowBlueDot(
                        userRole.toString()
                    ),
                    mapStyleOptions = mapStyleOptions
                )
            ) {
                if (latLngList.isNotEmpty()) {
                    Polyline(
                        points = fullPolylineList,
                        color = Color.Blue,
                        width = 24f,
                        visible = true,
                        geodesic = true, // Makes lines follow Earth curvature
                        clickable = false,
                        jointType = JointType.ROUND, // Smooth corners
                        startCap = RoundCap(),   // ✅ Rounded start
                        endCap = SquareCap(),    // ✅ Flat end
                    )
                    Marker(
                        state = MarkerState(position = latLngList.last()),
                        icon = bitmapDescriptorFromVector(
                            context,
                            if (userRole == Constants.USER_PARENT && isDriverInactive.value) R.drawable.red_marker else R.drawable.green_marker,
                            width = 120,
                            height = 120
                        ),
                        rotation = bearing,
                        anchor = Offset(0.5f, 0.5f),
                        flat = true
                    )
                }
            }

            if (showExitDialog && userRole.equals(Constants.USER_DRIVER)) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = {
                        Text(text = "Stop Tracking?")
                    },
                    text = {
                        Text("Tracking is currently active. Do you want to exit and stop tracking?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            viewModel.stopTracking()
                            if (userRole == Constants.USER_DRIVER) {
                                viewModel.stopDriverTracking(userId, Constants.INACTIVE_TRACKING)
                            }
                            // Exit the screen (use NavController if you're using Navigation)
                            (context as? Activity)?.finish()
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Timer display on map
            if (userRole == Constants.USER_PARENT && !assignedVehicleId.isNullOrBlank() && vehicleTrackingStatus.equals(
                    PlaceHolders.ACCEPTED
                )
            ) {
                RadarTimerWithProgress(
                    timer = timer.value,
                    isDriverInactive = isDriverInactive.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isDriverInactive.value && !isTracking) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "🚌❌ Vehicle owner hasn't started tracking yet",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // Optional side padding
                            textAlign = TextAlign.Center
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.stopTracking()
                            viewModel.clearParentRoute()
                            latLngList.lastOrNull()?.let { latestLatLng ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(latestLatLng, 18f),
                                        1000
                                    )
                                }
                            }
                            onRefresh() // 🔁 Triggers full screen recomposition
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 150.dp, end = 16.dp)
                            .size(48.dp) // Circular button size
                            .background(Color(0xFF2196F3), shape = CircleShape)
                            .shadow(6.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else if (isDriverInactive.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "😞 Live Tracking not shown\nRefresh the Screen",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // Optional side padding
                            textAlign = TextAlign.Center
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.stopTracking()
                            viewModel.clearParentRoute()
                            latLngList.lastOrNull()?.let { latestLatLng ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(latestLatLng, 18f),
                                        1000
                                    )
                                }
                            }
                            onRefresh() // 🔁 Triggers full screen recomposition
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 150.dp, end = 16.dp)
                            .size(48.dp) // Circular button size
                            .background(Color(0xFF2196F3), shape = CircleShape)
                            .shadow(6.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 🟢 Driver-only Start/Stop button
            if (userRole == Constants.USER_DRIVER) {
                val greenColor = Color(0xFF4CAF50) // Original Material Design Green (500)
                val redColor = Color(0xFFF44336)   // Original Material Design Red (500)

                Button(
                    onClick = {
                        if (isTracking) {
                            viewModel.stopTracking()
                            viewModel.stopDriverTracking(userId, Constants.INACTIVE_TRACKING)
                        } else {
                            if (locationPermissionState.status.isGranted) {
                                checkAndPromptEnableGps()
                            } else {
                                locationPermissionState.launchPermissionRequest()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) redColor else greenColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp,
                        focusedElevation = 10.dp,
                        hoveredElevation = 10.dp
                    ),
                    shape = RoundedCornerShape(16.dp), // Rounded look
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)) // Extra visual depth
                ) {
                    Text(
                        color = Color.White,
                        text = if (isTracking) Constants.STOP else Constants.START,
                        fontWeight = FontWeight.Bold
                    )

                }

            }

            // 🚫 Parent – show driver not assigned message
            if (userRole == Constants.USER_PARENT && (assignedVehicleId.isNullOrBlank() || !vehicleTrackingStatus.equals(
                    PlaceHolders.ACCEPTED, ignoreCase = true
                ))
            ) {
                CustomMessage(
                    PlaceHolders.MSG_NO_VEHICLE_ASSIGNED,
                    PlaceHolders.MSG_REQUEST_DRIVER_ASSIGNMENT
                )
            } else if (userRole == Constants.USER_PARENT && !assignedVehicleId.isNullOrBlank()
                && vehicleTrackingStatus.equals(PlaceHolders.REJECTED, ignoreCase = true)
            ) {
                CustomMessage(
                    PlaceHolders.MSG_NO_VEHICLE_ASSIGNED,
                    PlaceHolders.MSG_REQUEST_DRIVER_REJECTED
                )
            } else if (today != null && trial != null && paymentStatus == null && today.after(trial) || today == trial) {// trial period send assigned request to driver
                LaunchedEffect(Unit) {
                    showPaymentDialog = true
                    hasTriggeredPaymentDialog.value = true
                }
            }
        }
    }
    BackHandler(enabled = isTracking && userRole.equals(Constants.USER_DRIVER)) {
        showExitDialog = true
    }
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Trial Expired") },
            text = {
                Text("Your trial period has ended. Please complete payment to continue using the app.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.getRazorPayOrderId()
                        showPaymentDialog = false
                        redirectToPayment = true // trigger Razorpay bottom sheet
                        showSheet.value = true
                    }
                ) {
                    Text("Proceed to Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (redirectToPayment) {
        val fees = razorAmount?.div(100)
        val duration = "12 Months"
        val total = fees ?: 0

        if (showSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showSheet.value = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "\uD83D\uDE90 Complete the Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    Text("Duration: $duration", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Total: ₹$total",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showSheet.value = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showSheet.value = false
                                redirectToPayment = false // Reset state to avoid repeat triggering
                                if (activity != null) {
                                    activity.startPayment(razorAmount, razorOrderId)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Unable to start payment.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}


fun bitmapDescriptorFromVector(
    context: Context, @DrawableRes vectorResId: Int, width: Int = 100, height: Int = 100
): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun isLocationPermissionGranted(context: android.content.Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun CurrentLocationFab(
    viewModel: LatLngViewModel,
    cameraPositionState: CameraPositionState,
    currentLocation: MutableState<LatLng?>,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()
    var isCompassMode by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = {
            coroutineScope.launch {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng

                    // Toggle icon mode and adjust camera behavior
                    if (isCompassMode) {
                        // Compass mode (tilted + bearing follows movement)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder()
                                    .target(latLng)
                                    .zoom(18f)
                                    .tilt(45f)
                                    .bearing(90f) // You can dynamically fetch device orientation
                                    .build()
                            ),
                            durationMs = 1000
                        )
                    } else {
                        // Standard mode (zoom to current location)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder()
                                    .target(latLng)
                                    .zoom(18f)
                                    .tilt(0f)
                                    .bearing(0f)
                                    .build()
                            ),
                            durationMs = 1000
                        )
                    }

                    // Toggle mode
                    isCompassMode = !isCompassMode
                } ?: run {
                    Toast.makeText(
                        context,
                        PlaceHolders.MSG_LOCATION_NOT_AVAILABLE,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .size(56.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Color(0xFF4CAF50),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
            focusedElevation = 10.dp,
            hoveredElevation = 10.dp
        )
    ) {
        Icon(
            painter = painterResource(
                id = if (isCompassMode) R.drawable.ic_compass else R.drawable.ic_current_location
            ),
            contentDescription = if (isCompassMode)
                "Compass Mode"
            else
                "Current Location",
            modifier = Modifier.size(30.dp),
            tint = Color.Unspecified
        )
    }
}

fun shouldShowBlueDot(userType: String): Boolean {
    return userType.lowercase() in listOf("other") // if added driver or parent then blue dot will be show
}