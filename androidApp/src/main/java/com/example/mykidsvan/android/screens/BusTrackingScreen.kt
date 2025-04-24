package com.example.mykidsvan.android.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.R
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BusTrackingScreen(
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

   // val studentLatLongState = viewModel.getLatLongResponse.collectAsState()
    val userId by viewModel.userId.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    val previousLocations = remember { mutableStateMapOf<Int, LatLng>() }
    val locationPaths = remember { mutableStateMapOf<Int, MutableList<LatLng>>() }
    val markerStates = remember { mutableStateMapOf<Int, MarkerState>() }

    var timer by remember { mutableStateOf(15) }

    // Permission state
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val mapProperties = remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = false))
    }

    // Ask for permission at first launch
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    // Move to current location if permission is granted
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            mapProperties.value = MapProperties(isMyLocationEnabled = true)
            try {
                val location = Tasks.await(fusedLocationProviderClient.lastLocation)
                location?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        ),
                        durationMs = 1000
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Timer and periodic fetch
    LaunchedEffect(userId) {
        while (true) {
            repeat(15) {
                timer = 15 - it
                delay(1000)
            }
            userId?.let {
                viewModel.getLatLong("1", it)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Countdown: ${timer}s",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

//        studentLatLongState.value?.let { response ->
//            if (response.status == true && response.data.isNotEmpty()) {
//                val latestVehicle = response.data.first()
//                Text(
//                    text = "Vehicle No: ${latestVehicle.id ?: "Unknown"}",
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                )
//                Text(
//                    text = "Last Updated: ${latestVehicle.timer ?: "N/A"}",
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                )
//            }
//        }

        // If permission granted, show the map
        if (permissionState.status.isGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties.value,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                cameraPositionState = cameraPositionState
            ) {
//                studentLatLongState.value?.data?.forEach { vehicle ->
//                    val lat = vehicle.latitude?.toDoubleOrNull() ?: return@forEach
//                    val lng = vehicle.longitude?.toDoubleOrNull() ?: return@forEach
//                    val driverId = vehicle.id?.toIntOrNull() ?: return@forEach
//                    val newPosition = LatLng(lat, lng)
//
//                    val path = locationPaths.getOrPut(driverId) { mutableListOf() }
//                    if (path.isEmpty() || path.last() != newPosition) {
//                        path.add(newPosition)
//                    }
//
//                    val markerState = markerStates.getOrPut(driverId) {
//                        MarkerState(position = newPosition)
//                    }
//
//                    val previousPosition = previousLocations[driverId]
//                    if (previousPosition != null && previousPosition != newPosition) {
//                        LaunchedEffect(newPosition) {
//                            val steps = 10
//                            for (i in 1..steps) {
//                                val latStep = previousPosition.latitude + (newPosition.latitude - previousPosition.latitude) * i / steps
//                                val lngStep = previousPosition.longitude + (newPosition.longitude - previousPosition.longitude) * i / steps
//                                markerState.position = LatLng(latStep, lngStep)
//                                delay(50)
//                            }
//                        }
//                    } else {
//                        markerState.position = newPosition
//                    }
//
//                    Marker(
//                        state = markerState,
//                        title = vehicle.driverType,
//                        icon = bitmapDescriptorFromRes(context, R.drawable.busyellow),
//                        flat = true,
//                        rotation = if (previousPosition != null) getBearing(previousPosition, newPosition) else 0f,
//                        onClick = {
//                            Toast.makeText(context, "${vehicle.driverType} clicked", Toast.LENGTH_SHORT).show()
//                            false
//                        }
//                    )
//
//                    Polyline(
//                        points = path,
//                        color = Color.Blue,
//                        width = 6f
//                    )
//
//                    previousLocations[driverId] = newPosition
//                }
            }
        } else {
            // If denied or not available, show a message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Location permission required to show the map.")
            }
        }
    }
}


fun getBearing(start: LatLng, end: LatLng): Float {
    val startLat = Math.toRadians(start.latitude)
    val startLng = Math.toRadians(start.longitude)
    val endLat = Math.toRadians(end.latitude)
    val endLng = Math.toRadians(end.longitude)

    val dLon = endLng - startLng
    val y = sin(dLon) * cos(endLat)
    val x = cos(startLat) * sin(endLat) - sin(startLat) * cos(endLat) * cos(dLon)

    return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
}

@Composable
fun bitmapDescriptorFromRes(context: Context, resId: Int): BitmapDescriptor {
    val drawable =
        ContextCompat.getDrawable(context, resId) ?: return BitmapDescriptorFactory.defaultMarker()
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}