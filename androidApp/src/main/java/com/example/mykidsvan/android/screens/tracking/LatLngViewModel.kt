package com.example.maptracking
import android.Manifest

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.screens.tracking.isLocationPermissionGranted
import com.example.mykidsvan.android.utils.UserPreferences
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class LatLngViewModel(
    private val userPreferences: UserPreferences,// Inject via constructor
    private val repository: LatLngRepository,
    private val context: Context
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    private val _latLngList = MutableStateFlow<List<LatLng>>(emptyList())
    val latLngList: StateFlow<List<LatLng>> = _latLngList

    private val _bearing = MutableStateFlow(0f)
    val bearing: StateFlow<Float> = _bearing

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private var trackingJob: Job? = null
    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    private val _assignedVehicleId = MutableStateFlow<String?>(null)
    val assignedVehicleId: StateFlow<String?> = _assignedVehicleId

    private val _vehicleStatus = MutableStateFlow<String?>(null)
    val vehicleStatus: StateFlow<String?> = _vehicleStatus

    var driverId:String? = null// Your default/fallback

    init {
        viewModelScope.launch {
            // 1. Get assignedVehicleId from preferences
            userPreferences.assignVehicleIdFlow.collect { vehicleId ->
                _assignedVehicleId.value = vehicleId

                // 2. If not null, save the driverId to preferences
                if (vehicleId != null) {
                    driverId = vehicleId
                }
            }
        }

        viewModelScope.launch {
            fetchInitialLocation()
        }
    }

    private suspend fun fetchInitialLocation() {
        if (!isLocationPermissionGranted(context)) return
        val location = getLastKnownLocation()
        location?.let {
            _currentLocation.value = LatLng(it.latitude, it.longitude)
        }
    }



    fun startTracking() {
        _isTracking.value = true
        trackingJob = viewModelScope.launch {
            while (isActive) {
                sendCurrentLocationToServer()
                fetchLatLngFromServer()
                delay(10_000) // Wait 10 seconds
            }
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        trackingJob?.cancel()
    }

    private suspend fun sendCurrentLocationToServer() {
        if (!isLocationPermissionGranted(context)) return

        val location = getLastKnownLocation()
        location?.let {
            try {
                driverId?.let { it1 ->
                    SendLatLongRequest(
                        id = it1,
                        latitude = it.latitude.toString(),
                        longitude = it.longitude.toString()
                    )
                }?.let { it2 ->
                    repository.sendLatLong(
                        it2
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            cont.resume(null, null) // No location permission, return null safely
            return@suspendCancellableCoroutine
        }

        fusedLocationProvider.lastLocation
            .addOnSuccessListener { location -> cont.resume(location, null) }
            .addOnFailureListener { cont.resume(null, null) }
    }


    private suspend fun fetchLatLngFromServer() {
        try {
            val response = driverId?.let { repository.getLatLong(it) }
            if (response?.status == true && response.data.isNotEmpty()) {
                val newLatLng = response.data.firstOrNull()?.let {
                    LatLng(
                        it.latitude?.toDoubleOrNull() ?: return,
                        it.longitude?.toDoubleOrNull() ?: return
                    )
                }

                newLatLng?.let { latLng ->
                    val oldLatLng = _latLngList.value.lastOrNull()
                    _bearing.value = oldLatLng?.let { old ->
                        calculateBearing(old, latLng)
                    } ?: 0f
                    _latLngList.update { it + latLng }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun calculateBearing(start: LatLng, end: LatLng): Float {
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)
        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return (Math.toDegrees(atan2(y, x)).toFloat() + 360) % 360
    }


}
