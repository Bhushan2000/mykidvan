package com.example.mykidsvan.android.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    var isTracking by remember { mutableStateOf(false) }
    var shouldFollow by remember { mutableStateOf(true) }

    val currentLatLng = remember { mutableStateOf<LatLng?>(null) }
    val serverLatLngList = remember { mutableStateListOf<LatLng>() }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // States for animation
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var currentIndex by remember { mutableStateOf(0) }
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var bearing by remember { mutableStateOf(0f) }
    var totalDistance by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Request location permission
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // LocationCallback for continuous updates
    val locationCallback = rememberUpdatedState(newValue = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLatLng.value = latLng
            }
        }
    })

    // Start or stop location updates based on tracking state
    LaunchedEffect(isTracking) {
        if (locationPermissionState.status.isGranted) {
            if (isTracking) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 2000
                    fastestInterval = 1000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback.value,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationClient.removeLocationUpdates(locationCallback.value)
            }
        }
    }

    // Send location to server every 10 sec if changed
    var lastSentLatLng by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (isActive) {
                currentLatLng.value?.let { latLng ->
                    if (latLng != lastSentLatLng) {
                        Log.d("LocationTracking", "📤 Sending new location: $latLng")
                        viewModel.sendLatLong(
                            lat = latLng.latitude.toString(),
                            long = latLng.longitude.toString(),
                            id = userId
                        )
                        lastSentLatLng = latLng
                    } else {
                        Log.d("LocationTracking", "⏭️ Skipping send, location unchanged: $latLng")
                    }
                }
                delay(15_000)
            }
        }
    }

    // Fetch updated lat-long list every 10 sec and add only new points
    var lastFetchedLatLng by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (isActive) {
                val listFromServer = viewModel.getLatLong(userId)
                if (listFromServer.isNotEmpty()) {
                    val newLast = listFromServer.last()
                    if (newLast != lastFetchedLatLng) {
                        Log.d(
                            "LocationTracking",
                            "📥 Fetched new data from server. Last point: $newLast"
                        )

                        val lastKnown = serverLatLngList.lastOrNull()
                        val newPoints = if (lastKnown != null) {
                            listFromServer.dropWhile { it == lastKnown }
                        } else {
                            listFromServer
                        }

                        serverLatLngList.addAll(newPoints)
                        lastFetchedLatLng = newLast

                        if (serverLatLngList.size == 1) {
                            serverLatLngList.add(serverLatLngList[0])
                        }

                        if (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0) {
                            currentLocation = serverLatLngList.first()
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation,
                                    17f
                                )
                            )
                        }
                    } else {
                        Log.d(
                            "LocationTracking",
                            "⏭️ Skipping fetch, server data unchanged: $newLast"
                        )
                    }
                }
                delay(15_000)
            }
        }
    }

    // Animate marker and polyline continuously as new points arrive
    LaunchedEffect(isTracking) {
        if (isTracking) {
            animationJob = CoroutineScope(Dispatchers.Main).launch {
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

    // ------------------- UI -------------------
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { shouldFollow = false }
        ) {
            currentLatLng.value?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    icon = bitmapDescriptorFromVector(
                        context,
                        R.drawable.baseline_my_location_24,
                        50,
                        50
                    ),
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

        val coroutineScope = rememberCoroutineScope()
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
                shape = CircleShape, // makes it circular
                containerColor = MaterialTheme.colorScheme.primary, // optional: set background color
                contentColor = Color.White, // optional: set icon color
                modifier = Modifier.size(56.dp) // standard FAB size
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_my_location_24),
                    contentDescription = "Current Location",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White) // 👈 makes the icon white

                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
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
            }) {
                Text(if (isTracking) "Stop Tracking" else "Start Tracking")
            }

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Distance: ${"%.2f".format(totalDistance)} m",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Speed: ${"%.2f".format(speed)} m/s",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Last Point: ${serverLatLngList.lastOrNull() ?: "No data"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
