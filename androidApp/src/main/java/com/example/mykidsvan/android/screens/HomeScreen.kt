package com.example.mykidsvan.android.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R
import com.example.mykidsvan.android.utils.SharedPrefUtil
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
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
fun HomeScreen(viewModel: AuthViewModel, schoolId: String, userId: String) {
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
            if (locationPermissionState.status.isGranted) {
                GoogleMapWithControls(

                ) // Show the map if permission is granted

//                RealTimeTrackingScreen(
//                    viewModel,
//                    userId,
//                    schoolId
//                )

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

//@SuppressLint("MissingPermission")
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun GoogleMapWithControls(
//    viewModel: AuthViewModel,
//    schoolId: String,
//    userId: String
//) {
//    val context = LocalContext.current
//    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
//
//    val mapZoom by remember { mutableStateOf(17f) }
//    val currentLatLng = remember { mutableStateOf<LatLng?>(null) }
//    val serverLatLngList = remember { mutableStateListOf<LatLng>() }
//    val cameraPositionState = rememberCameraPositionState()
//    val icon = rememberAsyncMarkerIcon(R.drawable.busyellow, 32, 48)
//
//    // Get current location once
//    LaunchedEffect(locationPermissionState.status.isGranted) {
//        if (locationPermissionState.status.isGranted) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                location?.let {
//                    currentLatLng.value = LatLng(it.latitude, it.longitude)
//                }
//            }
//        } else {
//            locationPermissionState.launchPermissionRequest()
//        }
//    }
//
//    // Send current location to server every 10 seconds
//    LaunchedEffect(userId) {
//        while (true) {
//            currentLatLng.value?.let { latLng ->
//                viewModel.sendLatLong(
//                    lat = latLng.latitude.toString(),
//                    long = latLng.longitude.toString(),
//                    schoolId = schoolId,
//                    id = userId
//                )
//            }
//            delay(10_000)
//        }
//    }
//
//    // Fetch path from server every 10 seconds
//    LaunchedEffect(userId) {
//        while (true) {
//            val listFromServer = viewModel.getLatLong(schoolId, userId)
//            if (listFromServer.isNotEmpty()) {
//                serverLatLngList.clear()
//                serverLatLngList.addAll(listFromServer)
//            }
//            delay(10_000)
//        }
//    }
//
//    // Animate camera on latest position
//    LaunchedEffect(serverLatLngList.size) {
//        serverLatLngList.lastOrNull()?.let { last ->
//            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(last, mapZoom))
//        }
//    }
//
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = cameraPositionState,
//        properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
//        uiSettings = MapUiSettings(zoomControlsEnabled = false)
//    ) {
//        // Draw polyline
//        if (serverLatLngList.size > 1) {
//            Polyline(
//                points = serverLatLngList,
//                color = Color.Blue,
//                width = 6f
//            )
//        }
//
//        // Draw marker
//        serverLatLngList.lastOrNull()?.let { current ->
//            val previous = serverLatLngList.getOrNull(serverLatLngList.lastIndex - 1)
//            val rotation = if (previous != null) getBearingHome(previous, current) else 0f
//
//            icon.value?.let { iconBitmap ->
//                Marker(
//                    state = MarkerState(position = current),
//                    title = "Bus",
//                    icon = iconBitmap,
//                    rotation = rotation,
//                    anchor = Offset(0.5f, 0.5f),
//                    flat = true
//                )
//            }
//        }
//    }
//
//    if (!locationPermissionState.status.isGranted) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .wrapContentSize(Alignment.Center),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Location permission is required to display the map.")
//                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
//                    Text("Grant Permission")
//                }
//            }
//        }
//
//    }
//}


@Composable
fun GoogleMapWithControls() {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var isTracking by remember { mutableStateOf(false) }
    var shouldFollow by remember { mutableStateOf(true) }

    val forwardPath = listOf(
        LatLng(21.0956675, 79.1349206), // Start: Nagpur

        // Right turn
        LatLng(21.0960, 79.1360),
        LatLng(21.0965, 79.1375),

        // Left turn
        LatLng(21.0968, 79.1365),
        LatLng(21.0972, 79.1350),

        // U-turn
        LatLng(21.0965, 79.1340),
        LatLng(21.0958, 79.1335),
        LatLng(21.0950, 79.1340),

        // Zig-zag
        LatLng(21.0955, 79.1345),
        LatLng(21.0960, 79.1340),
        LatLng(21.0965, 79.1345),
        LatLng(21.0970, 79.1340),

        // Blind turn (sudden direction change)
        LatLng(21.0975, 79.1342),
        LatLng(21.0979, 79.1349),

        // Traffic road (short pause and slow movement)
        LatLng(21.0985, 79.1351),
        LatLng(21.0990, 79.1353),
        LatLng(21.0995, 79.1355),

        // Crowded road (dense small changes)
        LatLng(21.1000, 79.1356),
        LatLng(21.1002, 79.1357),
        LatLng(21.1004, 79.1358),
        LatLng(21.1006, 79.1359),

        // Work-in-progress road (detour and loop)
        LatLng(21.1010, 79.1360),
        LatLng(21.1015, 79.1355),
        LatLng(21.1020, 79.1350),
        LatLng(21.1015, 79.1345),
        LatLng(21.1010, 79.1340),

        // Move North (Top)
        LatLng(21.1030, 79.1340),
        LatLng(21.1045, 79.1340),

        // Move South (Down)
        LatLng(21.1030, 79.1340),
        LatLng(21.1015, 79.1340),

        // Side turn (East/West)
        LatLng(21.1015, 79.1350),
        LatLng(21.1015, 79.1360),

        // Reverse
        LatLng(21.1000, 79.1355),
        LatLng(21.0990, 79.1350),

        // Final point (end of route)
        LatLng(21.0980, 79.1345)
    )


    val returnPath = forwardPath.reversed()

    // Combined forward and return path
    val simulatedPath = forwardPath + returnPath

    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var currentIndex by remember { mutableStateOf(0) }
    var currentLocation by remember { mutableStateOf(simulatedPath.first()) }
    var bearing by remember { mutableStateOf(0f) }
    var totalDistance by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }

    var animationJob by remember { mutableStateOf<Job?>(null) }

    // Default camera focus on the start
    LaunchedEffect(Unit) {
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
    }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            animationJob = launch {
                while (isTracking && currentIndex < simulatedPath.size - 1) {
                    val start = simulatedPath[currentIndex]
                    val end = simulatedPath[currentIndex + 1]

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
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    interpolated,
                                    17f
                                )
                            )
                        }

                        delay(30L)
                    }
                    currentIndex++
                }
            }
        } else {
            animationJob?.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { shouldFollow = false }
        ) {
            if (pathPoints.isNotEmpty()) {
                Polyline(
                    points = pathPoints,
                    color = Color.Blue,
                    width = 20f
                )

                Marker(
                    state = MarkerState(position = currentLocation),
                    icon = bitmapDescriptorFromVector(context, R.drawable.busyellow, 50, 80),
                    rotation = bearing,
                    anchor = Offset(0.5f, 0.5f),
                    flat = true
                )
            }
        }

        FloatingActionButton(
            onClick = {
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_my_location_24), // Replace with your drawable resource
                contentDescription = "Current Location",
                modifier = Modifier.size(24.dp) // You can adjust the size as needed
            )
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
                    currentIndex = 0
                    animationJob?.cancel()
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
                }
            }
        }
    }
}


// Function to calculate bearing (angle) between two LatLng points
fun getBearingHome(start: LatLng, end: LatLng): Float {
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

    return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
}


// Function to asynchronously load and scale marker icon
@Composable
fun rememberAsyncMarkerIcon(
    @DrawableRes resId: Int,
    width: Int,
    height: Int
): State<BitmapDescriptor?> {
    val context = LocalContext.current
    val bitmapDescriptorState = remember { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(resId, width, height) {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, false)
        bitmapDescriptorState.value = BitmapDescriptorFactory.fromBitmap(resized)
    }
    return bitmapDescriptorState
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RealTimeTrackingScreen(viewModel: AuthViewModel, userId: String, schoolId: String) {
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

    // Request permission and get current location
    LaunchedEffect(Unit) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLatLng.value = LatLng(it.latitude, it.longitude)
                    currentLocation = currentLatLng.value!!
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Periodic sending of location to server
    LaunchedEffect(currentLatLng.value, userId) {
        while (true) {
            currentLatLng.value?.let { latLng ->
                viewModel.sendLatLong(
                    lat = latLng.latitude.toString(),
                    long = latLng.longitude.toString(),
                    schoolId = "1",
                    id = userId
                )
            }
            delay(10_000)
        }
    }

    // Periodic fetching of location list from server
    LaunchedEffect(userId) {
        while (true) {
            val listFromServer = viewModel.getLatLong(schoolId, userId)
            if (listFromServer.isNotEmpty()) {
                serverLatLngList.clear()
                serverLatLngList.addAll(listFromServer)
                if (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0) {
                    currentLocation = serverLatLngList.first()
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
                }
            }
            delay(10_000)
        }
    }

    // Animate movement
    LaunchedEffect(isTracking) {
        if (isTracking) {
            animationJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive && currentIndex < serverLatLngList.size - 1) {
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
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(interpolated, 17f)
                            )
                        }

                        delay(30L)
                    }

                    currentIndex++
                }
            }
        } else {
            animationJob?.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { shouldFollow = false }
        ) {
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

        FloatingActionButton(
            onClick = {
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_my_location_24), // Replace with your drawable resource
                contentDescription = "Current Location",
                modifier = Modifier.size(24.dp) // You can adjust the size as needed
            )
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
                    currentIndex = 0
                    pathPoints = emptyList()
                    totalDistance = 0f
                    speed = 0f
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

fun calculateBearing(start: LatLng, end: LatLng): Float {
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
}

fun calculateDistanceInMeters(start: LatLng, end: LatLng): Float {
    val result = FloatArray(1)
    Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, result)
    return result[0]
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
