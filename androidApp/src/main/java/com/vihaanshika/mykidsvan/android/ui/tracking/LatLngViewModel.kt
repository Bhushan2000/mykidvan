package com.example.maptracking

import android.Manifest

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vihaanshika.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.vihaanshika.mykidsvan.android.ui.tracking.LatLngRepository
import com.vihaanshika.mykidsvan.android.ui.tracking.isLocationPermissionGranted
import com.vihaanshika.mykidsvan.android.ui.tracking.LocationTrackingService
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.vihaanshika.mykidsvan.android.utils.UserPreferences
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

    private val _vehicleTrackingStatus = MutableStateFlow<String?>(null)
    val vehicleTrackingStatus: StateFlow<String?> = _vehicleTrackingStatus

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    var driverId: String? = null// Your default/fallback

    private var locationReceiver: BroadcastReceiver? = null

    // user id
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        // Collect user role
        viewModelScope.launch {
            userPreferences.userRole.collect { role ->
                _userRole.value = role

                // 👇 After role is fetched, update driverId if role is "driver"
                if (role == "driver") {
                    userPreferences.userIdFlow.collect { id ->
                        driverId = id
                    } // or fetchUserIdFromPreferences() if not flow
                }
            }
        }

        // Collect assigned vehicle ID
        viewModelScope.launch {
            userPreferences.assignVehicleIdFlow.collect { vehicleId ->
                _assignedVehicleId.value = vehicleId

                // 👇 Only assign if user is NOT driver (e.g., parent case)
                if (_userRole.value != "driver" && vehicleId != null) {
                    driverId = vehicleId
                }
            }
        }

        // Collect tracking status
        viewModelScope.launch {
            userPreferences.statusFlow.collect { status ->
                _vehicleTrackingStatus.value = status
            }
        }

        // Collect userId
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { id ->
                _userId.value = id

                // 👇 In case role is already "driver", assign here too
                if (_userRole.value == "driver") {
                    driverId = id
                }
            }
        }

        // Initial location fetch
        viewModelScope.launch {
            fetchInitialLocation()
        }
    }

    private suspend fun fetchInitialLocation() {
        if (userRole.value != "driver") return // Skip for parents

        if (!isLocationPermissionGranted(context)) return

        val location = getLastKnownLocation()
        location?.let {
            _currentLocation.value = LatLng(it.latitude, it.longitude)
        }
    }

    fun startTracking() {
        val role = userRole.value
        _isTracking.value = true
         trackingJob = viewModelScope.launch {
            while (isActive) {
                // Driver → send and receive
                if (role == "driver") {
                    // Keep fallback for ViewModel polling
                    val location = getLastKnownLocation()
                    location?.let { sendCurrentLocationToServer(it) }
                    fetchLatLngFromServer()
                }
                // Parent → only receive
                else if (role == "parent") {
                    fetchLatLngFromServer()
                }

                delay(10_000)
            }
        }
    }

    fun registerLocationBroadcastReceiver(context: Context) {
        if (locationReceiver != null) return // Avoid re-registering

        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val lat = intent?.getDoubleExtra("latitude", 0.0)
                val lng = intent?.getDoubleExtra("longitude", 0.0)

                if (lat != null && lng != null) {
                    val location = Location("service").apply {
                        latitude = lat
                        longitude = lng
                    }

                    _currentLocation.value = LatLng(lat, lng)

                    viewModelScope.launch {
                        sendCurrentLocationToServer(location)
                    }
                }

            }
        }

        val filter = IntentFilter(Constants.LOCATION_BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(context).registerReceiver(locationReceiver!!, filter)
    }

    fun unregisterLocationBroadcastReceiver(context: Context) {
        locationReceiver?.let {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(it)
            locationReceiver = null
        }
    }


    fun startTrackingService(context: Context) {
        val intent = Intent(context, LocationTrackingService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopTrackingService(context: Context) {
        val intent = Intent(context, LocationTrackingService::class.java)
        context.stopService(intent)
    }

    fun stopTracking() {
        _isTracking.value = false
        trackingJob?.cancel()
        stopTrackingService(context)
    }

    private suspend fun sendCurrentLocationToServer(location: Location) {
        if (userRole.value != "driver") return // ✅ Prevents sending if the role is "parent"
        if (!isLocationPermissionGranted(context)) return
        try {
            driverId?.let { id ->
                val request = SendLatLongRequest(
                    id = id,
                    latitude = location.latitude.toString(),
                    longitude = location.longitude.toString()
                )
                repository.sendLatLong(request) // ✅ Sends location to server
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        if (userRole.value != "driver") {
            cont.resume(null, null)
            return@suspendCancellableCoroutine
        }

        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            cont.resume(null, null)
            return@suspendCancellableCoroutine
        }

        fusedLocationProvider.lastLocation
            .addOnSuccessListener { cont.resume(it, null) }
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

    fun hasAllPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}
