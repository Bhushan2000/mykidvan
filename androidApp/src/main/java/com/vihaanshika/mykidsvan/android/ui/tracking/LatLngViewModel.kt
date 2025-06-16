package com.example.maptracking

import android.Manifest

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
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
import com.vihaanshika.mykidsvan.android.data.dto.response.StopTrackingResponse
import com.vihaanshika.mykidsvan.android.utils.LocationFetcher
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    // for parent
    private val _latLngList = MutableStateFlow<List<LatLng>>(emptyList())
    val latLngList: StateFlow<List<LatLng>> = _latLngList

    // for driver
    private val _visiblePolylinePath = MutableStateFlow<List<LatLng>>(emptyList())
    val visiblePolylinePath: StateFlow<List<LatLng>> = _visiblePolylinePath

    // for driver
    private val _localLatLngList = MutableStateFlow<List<LatLng>>(emptyList())
    val localLatLngList: StateFlow<List<LatLng>> = _localLatLngList

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
    var trackingStatus: String? = null
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

    fun appendPolylinePoint(newPoint: LatLng) {
        _visiblePolylinePath.update { it + newPoint }
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
        _isTracking.value = true
        trackingJob = viewModelScope.launch {
            while (isActive) {
                val role = userRole.value
                if (role == Constants.USER_DRIVER) {
                    val location = getLastKnownLocation() // fallback
                    location?.let { sendCurrentLocationToServer(it) }

                } else if (role == Constants.USER_PARENT) {
//                    fetchLatLngFromServer()
                }
                delay(10_000)
            }
        }
    }

    fun registerLocationBroadcastReceiver(context: Context) {
        if (locationReceiver != null) return // Avoid re-registering

        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val action = intent?.action
                val lat = intent?.getDoubleExtra("latitude", 0.0)
                val lng = intent?.getDoubleExtra("longitude", 0.0)

                Log.d(
                    "BroadcastReceiver",
                    "Received broadcast action: $action, lat: $lat, lng: $lng"
                )

                if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                    val latLng = LatLng(lat, lng)
                    viewModelScope.launch {
                        when (action) {
                            Constants.SERVER_LOCATION_BROADCAST_ACTION -> {
                                if (userRole.value == Constants.USER_PARENT) {
                                    Log.d("DEBUG", "Inside USER_PARENT block")

                                    if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                                        val oldLatLng = _latLngList.value.lastOrNull()
                                        _bearing.value = oldLatLng?.let { old ->
                                            calculateBearing(old, latLng).takeIf { it.isFinite() }
                                        } ?: 0f

                                        // ✅ Skip if duplicate in visible path
                                        if (_visiblePolylinePath.value.lastOrNull()
                                                ?.let { it.latitude == latLng.latitude && it.longitude == latLng.longitude } != true
                                        ) {
                                            _visiblePolylinePath.update { oldList ->
                                                val updated = (oldList + latLng)
                                                updated
                                            }
                                        }

                                        // ✅ Skip if duplicate in latLng list
                                        if (_latLngList.value.lastOrNull()
                                                ?.let { it.latitude == latLng.latitude && it.longitude == latLng.longitude } != true
                                        ) {
                                            _latLngList.update { it + latLng }
                                            Log.e("Fetch----if", "onReceive: ${_latLngList.value}")
                                        } else {
                                            Log.d("DEBUG", "Duplicate latLng skipped: $latLng")
                                        }
                                    }

                                } else {
                                    Log.d("DEBUG", "userRole not parent: ${userRole.value}")
                                }
                            }

                            Constants.LOCATION_BROADCAST_ACTION -> {
                                if (userRole.value == Constants.USER_DRIVER) {
                                    val location = Location("service").apply {
                                        latitude = lat
                                        longitude = lng
                                    }
                                    sendCurrentLocationToServer(location)

                                }
                            }
                        }
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Constants.LOCATION_BROADCAST_ACTION)
            addAction(Constants.SERVER_LOCATION_BROADCAST_ACTION) // In case you're using it
        }
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
            val newLatLng = LatLng(location.latitude, location.longitude)

            // Check distance from previous point
            val lastLatLng = _visiblePolylinePath.value.lastOrNull()
            if (lastLatLng != null) {
                val lastLocation = Location("").apply {
                    latitude = lastLatLng.latitude
                    longitude = lastLatLng.longitude
                }

                val distance = lastLocation.distanceTo(location)
                if (distance < 5) {
                    Log.d("TAG", "Location change < 5m (${distance}m), skipping update.")
                    return
                }
            }
            val timestamp = getCurrentTimestamp()
            println(timestamp) // Example output: 2025-06-14 15:13:35

            val address = getFullAddress(context, location.latitude, location.longitude)
            Log.d("FullAddress", address ?: "Address not found")

            driverId?.let { id ->
                val request = SendLatLongRequest(
                    id = id,
                    latitude = location.latitude.toString(),
                    longitude = location.longitude.toString(),
                    start_time = timestamp,
                    location = address.toString(),
                    lat_status = Constants.ACTIVE_TRACKING
                )
                val response = repository.sendLatLong(request) // ✅ Sends location to server

             }

            val currentList = _localLatLngList.value

            val updatedList = when {
                currentList.isEmpty() -> listOf(newLatLng) // First point
                currentList.size == 1 && currentList[0] != newLatLng -> listOf(
                    currentList[0],
                    newLatLng
                ) // Second point
                currentList.size == 2 && currentList[1] != newLatLng -> listOf(
                    currentList[1],
                    newLatLng
                ) // Slide window
                else -> currentList // Same point as last, no update
            }

            _localLatLngList.value = updatedList

            // Animate marker with 2 points
            val shortList = _localLatLngList.value.toMutableList()
            if (shortList.isEmpty() || shortList.last() != newLatLng) {
                if (shortList.size >= 2) shortList.removeFirst()
                shortList.add(newLatLng)
                _localLatLngList.value = shortList
            }

            // Keep full path separately
            val fullPath = _visiblePolylinePath.value.toMutableList()
            if (fullPath.isEmpty() || fullPath.last() != newLatLng) {
                fullPath.add(newLatLng)
                _visiblePolylinePath.value = fullPath
            }

            // Calculate bearing between the two points
            if (updatedList.size == 2) {
                _bearing.value = calculateBearing(updatedList[0], updatedList[1])
            }
            Log.d("TAG", "sendCurrentLocationToServer: ${_localLatLngList.value}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getFullAddress(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = buildString {
                    append(address.featureName ?: "")        // House/building name
                    append(", ${address.thoroughfare ?: ""}") // Street name
                    append(", ${address.subLocality ?: ""}")  // Area/locality
                    append(", ${address.locality ?: ""}")     // City
                    append(", ${address.adminArea ?: ""}")    // State
                    append(", ${address.postalCode ?: ""}")   // ZIP
                    append(", ${address.countryName ?: ""}")  // Country
                }
                fullAddress.trim().replace(", ,", ",").replace(", ,", ",")
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    fun registerLocationReceiverIfNeeded(context: Context, role: String) {
        if (role == Constants.USER_DRIVER || role == Constants.USER_PARENT) {
            registerLocationBroadcastReceiver(context)
        }
    }

    fun unregisterLocationReceiverIfNeeded(context: Context, role: String) {
        if (role == Constants.USER_DRIVER || role == Constants.USER_PARENT) {
            unregisterLocationBroadcastReceiver(context)
        }
    }

    private val _stopTrackingState =
        MutableStateFlow<Resource<StopTrackingResponse>>(Resource.Loading())
    val stopTrackingState: StateFlow<Resource<StopTrackingResponse>> = _stopTrackingState

    fun stopDriverTracking(id: String, status: String) {
        viewModelScope.launch {
            _stopTrackingState.value = Resource.Loading()
            try {
                val response = repository.stopTracking(id, status)
                _stopTrackingState.value = Resource.Success(response)
            } catch (e: Exception) {
                e.printStackTrace()
                _stopTrackingState.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
