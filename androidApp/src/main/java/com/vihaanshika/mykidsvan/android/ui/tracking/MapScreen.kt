package com.vihaanshika.mykidsvan.android.ui.tracking

import com.google.maps.android.compose.Polyline
import com.google.android.gms.maps.model.JointType
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
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
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.maptracking.LatLngViewModel
import com.vihaanshika.mykidsvan.android.R
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: LatLngViewModel = koinViewModel(), userRole: String) {
    val context = LocalContext.current
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

    val fullPolylineList by viewModel.visiblePolylinePath.collectAsState()

    LaunchedEffect(isTracking) {
        if (isTracking) {
            viewModel.registerLocationReceiverIfNeeded(context, userRole.toString())
        } else {
            viewModel.unregisterLocationReceiverIfNeeded(context, userRole.toString())
        }
    }

    // this destroy service when page is leave
    DisposableEffect(userRole) {
        onDispose {
            viewModel.stopTracking() // Stop LiveData/StateFlow updates
        }
    }

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
            if (isTracking) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 18f), 1000)
            }
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
    ) {
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
                            width = 64,
                            height = 112
                        ),
                        rotation = bearing,
                        anchor = Offset(0.5f, 0.5f),
                        flat = true
                    )
                }
            }

            if (isDriverInactive.value && !assignedVehicleId.isNullOrBlank() &&
                vehicleTrackingStatus.equals(PlaceHolders.ACCEPTED, ignoreCase = true)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000)) // semi-transparent black
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PlaceHolders.MSG_TRACKING_NOT_STARTED,
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Timer display on map
            if (userRole == Constants.USER_PARENT)
                RadarTimerWithProgress(
                    timer = timer.value,
                    isDriverInactive = isDriverInactive.value
                )

            // 🟢 Driver-only Start/Stop button
            if (userRole == Constants.USER_DRIVER) {
                val greenColor = Color(0xFF4CAF50) // Original Material Design Green (500)
                val redColor = Color(0xFFF44336)   // Original Material Design Red (500)

                Button(
                    onClick = {
                        if (isTracking) {
                            viewModel.stopTracking()
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


                /*                FloatingActionButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val location = viewModel.getLastKnownLocation()
                                            location?.let {
                                                val latLng = LatLng(it.latitude, it.longitude)
                                                currentLocation.value = latLng
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newCameraPosition(
                                                        CameraPosition.builder()
                                                            .target(latLng)
                                                            .zoom(18f)
                                                            .tilt(45f)
                                                            .bearing(0f)
                                                            .build()
                                                    ),
                                                    durationMs = 1000
                                                )
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
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .size(56.dp), // Standard FAB size
                                    shape = CircleShape, // Ensure it's round
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = greenColor,
                                    elevation =  FloatingActionButtonDefaults.elevation(
                                        defaultElevation = 8.dp,
                                        pressedElevation = 12.dp,
                                        focusedElevation = 10.dp,
                                        hoveredElevation = 10.dp
                                    )

                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.menu),
                                        contentDescription = PlaceHolders.MSG_CURRENT_LOCATION,
                                        modifier = Modifier.size(40.dp), // Increased size
                                        tint = Color.Unspecified // <--- This disables the default black tint

                                    )
                                }*/

            }

            // 🚫 Parent – show driver not assigned message
            if (userRole == Constants.USER_PARENT && (assignedVehicleId.isNullOrBlank() || !vehicleTrackingStatus.equals(
                    PlaceHolders.ACCEPTED, ignoreCase = true
                ))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize(), // Fills the whole screen
                    contentAlignment = Alignment.Center // Centers the inner content
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp) // This adds margin around the whole message
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.8f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = PlaceHolders.MSG_NO_VEHICLE_ASSIGNED,
                                color = Color.Red,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = PlaceHolders.MSG_REQUEST_DRIVER_ASSIGNMENT,
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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
    return userType.lowercase() in listOf(Constants.USER_DRIVER)
}