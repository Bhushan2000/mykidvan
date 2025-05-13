package com.example.mykidsvan.android.screens
import android.provider.Settings
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Random
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: AuthViewModel, userId: String) {
    // Request permission for location
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Check location permission status and request if necessary
            Log.e("TAG", "HomeScreen: userId - $userId")
            if (locationPermissionState.status.isGranted) {
                RealTimeTrackingScreen(
                    viewModel,
                    userId,
                )
              } else {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Location permission is required to display the map.")
                    Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RealTimeTrackingScreen(viewModel: AuthViewModel, userId: String) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()

    var isTracking by remember { mutableStateOf(false) }
    var shouldFollow by remember { mutableStateOf(true) }
    var showEnableGpsDialog by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val currentLatLng = remember { mutableStateOf<LatLng?>(null) }
    val serverLatLngList = remember { mutableStateListOf<LatLng>() }
    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    var currentIndex by remember { mutableStateOf(0) }
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var bearing by remember { mutableStateOf(0f) }
    var totalDistance by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }

    var animationJob by remember { mutableStateOf<Job?>(null) }
    var lastSentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var lastFetchedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Check GPS availability
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Permission Request
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val locationCallback = rememberUpdatedState(newValue = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                currentLatLng.value = LatLng(it.latitude, it.longitude)
            }
        }
    })

    fun createLocationRequest(): LocationRequest =
        LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    // Start/Stop location updates
    LaunchedEffect(isTracking) {
        if (locationPermissionState.status.isGranted) {
            if (isTracking) {
                fusedLocationClient.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallback.value,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationClient.removeLocationUpdates(locationCallback.value)
            }
        }
    }

    // Send current location to server
    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (isActive) {
                currentLatLng.value?.let { latLng ->
                    if (latLng != lastSentLatLng) {
                        viewModel.sendLatLong(
                            lat = latLng.latitude.toString(),
                            long = latLng.longitude.toString(),
                            id = userId
                        )
                        lastSentLatLng = latLng
                    }
                }
                delay(5000)
            }
        }
    }

    // Fetch coordinates from server
    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (isActive) {
                val list = viewModel.getLatLong(userId)
                if (list.isNotEmpty()) {
                    val newLast = list.last()
                    if (newLast != lastFetchedLatLng) {
                        val lastKnown = serverLatLngList.lastOrNull()
                        val newPoints = if (lastKnown != null) {
                            list.dropWhile { it == lastKnown }
                        } else list

                        serverLatLngList.addAll(newPoints)
                        lastFetchedLatLng = newLast

                        if (serverLatLngList.size == 1) {
                            serverLatLngList.add(serverLatLngList[0])
                        }

                        if (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0) {
                            currentLocation = serverLatLngList.first()
                            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
                        }
                    }
                }
                delay(5000)
            }
        }
    }

    // Animate polyline + bus marker
    LaunchedEffect(isTracking) {
        if (isTracking) {
            animationJob = coroutineScope.launch {
                while (isActive) {
                    if (currentIndex >= serverLatLngList.size - 1) {
                        delay(500)
                        continue
                    }

                    val start = serverLatLngList[currentIndex]
                    val end = serverLatLngList[currentIndex + 1]

                    for (i in 0..100) {
                        val fraction = i / 100f
                        val interpolated = interpolate(start, end, fraction)
                        val distance = calculateDistanceInMeters(currentLocation, interpolated)

                        bearing = calculateBearing(start, end)
                        currentLocation = interpolated
                        pathPoints = pathPoints + interpolated
                        totalDistance += distance
                        speed = distance / 0.03f

                        if (shouldFollow) {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(interpolated, 17f),
                                durationMs = 1000
                            )
                        }
                        delay(5L)
                    }

                    currentIndex++
                }
            }
        } else {
            animationJob?.cancel()
        }
    }

    // -------------------- UI ---------------------
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { shouldFollow = false }
        ) {
            currentLatLng.value?.let {
                Marker(
                    state = MarkerState(position = it),
                    icon = bitmapDescriptorFromVector(context, R.drawable.baseline_my_location_24, 50, 50),
                    anchor = Offset(0.5f, 0.5f)
                )
            }

            if (pathPoints.isNotEmpty()) {
                Polyline(points = pathPoints, color = Color.Blue, width = 20f)
                Marker(
                    state = MarkerState(position = currentLocation),
                    icon = bitmapDescriptorFromVector(context, R.drawable.busyellow, 50, 80),
                    rotation = bearing,
                    anchor = Offset(0.5f, 0.5f),
                    flat = true
                )
            }
        }

        // My Location Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f),
                            durationMs = 1000
                        )
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_my_location_24),
                    contentDescription = "Current Location",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        // Tracking Button & Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                if (!isLocationEnabled()) {
                    showEnableGpsDialog = true
                } else {
                    isTracking = !isTracking
                    shouldFollow = true

                    if (!isTracking) {
                        animationJob?.cancel()
                        currentIndex = 0
                        pathPoints = emptyList()
                        totalDistance = 0f
                        speed = 0f
                        serverLatLngList.clear()
                        lastSentLatLng = null
                        lastFetchedLatLng = null
                    }
                }
            }) {
                Text(if (isTracking) "Stop Tracking" else "Start Tracking")
            }

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Distance: ${"%.2f".format(totalDistance)} m")
                    Text("Speed: ${"%.2f".format(speed)} m/s")
                    Text("Last Point: ${
                        serverLatLngList.lastOrNull()?.let {
                            "%.4f, %.4f".format(it.latitude, it.longitude)
                        } ?: "No data"
                    }")
                }
            }
        }

        // GPS Alert
        if (showEnableGpsDialog) {
            AlertDialog(
                onDismissRequest = { showEnableGpsDialog = false },
                title = { Text("Enable Location") },
                text = { Text("Location services are disabled. Please enable GPS to start tracking.") },
                confirmButton = {
                    TextButton(onClick = {
                        showEnableGpsDialog = false
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEnableGpsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


fun interpolate(start: LatLng, end: LatLng, fraction: Float): LatLng {
    val lat = (end.latitude - start.latitude) * fraction + start.latitude
    val lng = (end.longitude - start.longitude) * fraction + start.longitude
    return LatLng(lat, lng)
}

fun calculateDistanceInMeters(start: LatLng, end: LatLng): Float {
    val result = FloatArray(1)
    Location.distanceBetween(
        start.latitude, start.longitude,
        end.latitude, end.longitude,
        result
    )
    return result[0]
}

fun calculateBearing(start: LatLng, end: LatLng): Float {
    val startLocation = Location("").apply {
        latitude = start.latitude
        longitude = start.longitude
    }
    val endLocation = Location("").apply {
        latitude = end.latitude
        longitude = end.longitude
    }
    return startLocation.bearingTo(endLocation)
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