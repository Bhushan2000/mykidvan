package com.example.mykidsvan.android.screens.tracking

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
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.sp
import com.example.maptracking.LatLngViewModel
import com.example.mykidsvan.android.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: LatLngViewModel = koinViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val latLngList by viewModel.latLngList.collectAsState()
    val bearing by viewModel.bearing.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    val userRole by viewModel.userRole.collectAsState()
    val assignedVehicleId by viewModel.assignedVehicleId.collectAsState()
    val vehicleTrackingStatus by viewModel.vehicleTrackingStatus.collectAsState()

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
                        CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                        1000
                    )
                    viewModel.startTracking()
                }
            }
        } else {
            Toast.makeText(context, "GPS is required to start tracking", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndPromptEnableGps() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
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
                        CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                        1000
                    )
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
                    Toast.makeText(context, "Unable to request GPS enable", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Please enable GPS manually", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 🔴 Track if the driver is inactive for 40 seconds
    val isDriverInactive = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(latLngList.lastOrNull()) {
        if (userRole == "parent") {
            isDriverInactive.value = false // Reset inactivity

            delay(40_000) // Wait 40 seconds
            val lastPoint = latLngList.lastOrNull()

            // If no new update has come in 40 seconds
            if (lastPoint == latLngList.lastOrNull()) {
                isDriverInactive.value = true
                snackbarHostState.showSnackbar("Driver has not started tracking yet.")

            }
        }
    }

    // 🔁 Auto-start/stop tracking for parent based on assignedVehicleId and status
    LaunchedEffect(userRole, assignedVehicleId, vehicleTrackingStatus) {
        if (userRole == "parent") {
            if (!assignedVehicleId.isNullOrBlank() && vehicleTrackingStatus.equals(
                    "accepted",
                    ignoreCase = true
                )
            ) {
                if (!isTracking) {
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
        if (userRole == "driver") {
            if (!locationPermissionState.status.isGranted) {
                locationPermissionState.launchPermissionRequest()
            } else {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                        1000
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
    "stylers": [{"color": "#1d2c4d"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#8ec3b9"}]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{"color": "#1a3646"}]
  },
  {
    "featureType": "administrative.country",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#4b6878"}]
  },
  {
    "featureType": "administrative.land_parcel",
    "stylers": [{"visibility": "off"}]
  },
  {
    "featureType": "landscape.man_made",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#334e87"}]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#6f9ba5"}]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry.fill",
    "stylers": [{"color": "#023e58"}]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#3C7680"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{"color": "#304a7d"}]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#98a5be"}]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.stroke",
    "stylers": [{"color": "#1d2c4d"}]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{"color": "#2f3948"}]
  },
  {
    "featureType": "transit.station",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#d59563"}]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{"color": "#0e1626"}]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#4e6d70"}]
  }
]
""".trimIndent()

    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleOptions = remember {
        if (isDarkTheme) {
            MapStyleOptions(darkMapStyleJson)
        } else null // Default Google Map Light style
    }
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted,
                mapStyleOptions = mapStyleOptions
            )
        ) {
            if (latLngList.isNotEmpty()) {
                Polyline(points = latLngList, color = Color.Cyan, width = 20f)

                Marker(
                    state = MarkerState(position = latLngList.last()),
                    icon = bitmapDescriptorFromVector(
                        context,
                        if (userRole == "parent" && isDriverInactive.value) R.drawable.red_marker else R.drawable.green_marker,
                        100,
                        180
                    ),
                    rotation = bearing,
                    anchor = Offset(0.5f, 0.5f),
                    flat = true
                )
            }
        }

        // 🟢 Driver-only Start/Stop button
        if (userRole == "driver") {
            val greenColor = Color(0xFF99CC33) // Green color
            val redColor = Color(0xFFA03232)   // Red color
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
                modifier = Modifier
                    .wrapContentSize() // Makes the composable size itself based on its content
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(color = Color.White, text = if (isTracking) "Stop" else "Start")
            }
        }

        // 🚫 Parent – show driver not assigned message
        if (userRole == "parent" &&
            (assignedVehicleId.isNullOrBlank() || !vehicleTrackingStatus.equals(
                "accepted",
                ignoreCase = true
            ))
        ) {
            Text(
                text = "Driver not assigned",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }

        // Snackbar for parent
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}


fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int,
    width: Int = 100,
    height: Int = 100
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
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
