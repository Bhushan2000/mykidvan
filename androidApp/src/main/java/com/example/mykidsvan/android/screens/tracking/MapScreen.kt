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

    // Collect state from ViewModel
    val latLngList by viewModel.latLngList.collectAsState()
    val bearing by viewModel.bearing.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    // Launcher to show the system dialog to enable GPS
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // GPS enabled, start tracking and move camera to current location
            coroutineScope.launch {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1000)
                    viewModel.startTracking()
                }
            }
        } else {
            Toast.makeText(context, "GPS is required to start tracking", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check if GPS is enabled and prompt user if not
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
            // GPS already enabled, get current location and start tracking
            coroutineScope.launch {
                val location = viewModel.getLastKnownLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation.value = latLng
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1000)
                    viewModel.startTracking()
                }
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(context, "Unable to request GPS enable", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enable GPS manually", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Request location permission on first launch and set map camera to current location
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            val location = viewModel.getLastKnownLocation()
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation.value = latLng
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1000)
            }
        }
    }

    // Follow last position update to animate camera while tracking
    LaunchedEffect(latLngList.lastOrNull()) {
        latLngList.lastOrNull()?.let { newLatLng ->
            if (isTracking) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 18f), 1000)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted)
        ) {
            if (latLngList.isNotEmpty()) {
                Polyline(points = latLngList, color = Color.Cyan, width = 20f)

                Marker(
                    state = MarkerState(position = latLngList.last()),
                    icon = bitmapDescriptorFromVector(context, R.drawable.busyellow, 80, 120),
                    rotation = bearing,
                    anchor = Offset(0.5f, 0.5f),
                    flat = true
                )
            }
        }

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
                containerColor = if (isTracking) Color.Red else Color.Green
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(color = Color.White, text = if (isTracking) "Stop" else "Start",)
        }
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
