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
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentActiveInactiveResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.StopTrackingResponse
import com.vihaanshika.mykidsvan.android.ui.tracking.SimpleKalmanLatLong
import com.vihaanshika.mykidsvan.android.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
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

    // for parent
    private val _visiblePolylinePathParent = MutableStateFlow<List<LatLng>>(emptyList())
    val visiblePolylinePathParent: StateFlow<List<LatLng>> = _visiblePolylinePathParent

    // for driver
    private val _localLatLngList = MutableStateFlow<List<LatLng>>(emptyList())
    val localLatLngList: StateFlow<List<LatLng>> = _localLatLngList

    private val _bearing = MutableStateFlow(0f)
    val bearing: StateFlow<Float> = _bearing

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private var trackingJob: Job? = null
    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    private var _assignedVehicleId = MutableStateFlow<String?>(null)
    val assignedVehicleId: StateFlow<String?> = _assignedVehicleId

    private var _vehicleTrackingStatus = MutableStateFlow<String?>(null)
    val vehicleTrackingStatus: StateFlow<String?> = _vehicleTrackingStatus

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    var driverId: String? = null// Your default/fallback
    private var locationReceiver: BroadcastReceiver? = null

    // user id
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val kalmanFilter = SimpleKalmanLatLong(qMetresPerSecond = 3.0f)
    private var lastValidBearing: Float? = null
    var speed: Double? = 0.00

    private var lastBearingUpdateTime: Long = 0L
    private val MAX_RECENT_POINTS = 3

    private val _parentPollingStatus = MutableStateFlow<String?>(Constants.INACTIVE_TRACKING)
    val parentPollingStatus: StateFlow<String?> = _parentPollingStatus

    init {
        // Collect user role
        viewModelScope.launch {
            userPreferences.userRoleFlow.collect { role ->
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
        _isTracking.value = true
        trackingJob = viewModelScope.launch {
            while (isActive) {
                val role = userRole.value
                if (role == Constants.USER_DRIVER) {
                    val location = getLastKnownLocation() // fallback
                    location?.let { sendCurrentLocationToServer(it, speed) }
                }
//                else if (role == Constants.USER_PARENT) {
//                    fetchLatLngFromServer()
//                }
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
                var speedFromServer = intent?.getDoubleExtra("speed", 0.0)
                speed = speedFromServer
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
                                        // update tracking status
                                        val status = intent.getStringExtra("tracking_status")
                                        _parentPollingStatus.value = status
                                        Log.e("Broadcast", "Tracking status: ${parentPollingStatus.value}")

                                        val lastVisible =
                                            _visiblePolylinePathParent.value.lastOrNull()
                                        val lastLatLng = _latLngList.value.lastOrNull()

                                        val isNewPoint = lastLatLng?.let {
                                            it.latitude != latLng.latitude || it.longitude != latLng.longitude
                                        } ?: true

                                        if (isNewPoint) {
                                            // Update bearing only when new point is added
                                            _bearing.value = lastLatLng?.let { old ->
                                                calculateBearing(
                                                    old,
                                                    latLng
                                                ).takeIf { it.isFinite() }
                                            } ?: 0f

                                            // Add to _visiblePolylinePath if not already last
                                            if (lastVisible?.latitude != latLng.latitude || lastVisible.longitude != latLng.longitude) {
                                                _visiblePolylinePathParent.update { oldList -> oldList + latLng }
                                            }

                                            // Add to _latLngList
                                            _latLngList.update { it + latLng }
                                            Log.d("Fetch----if", "onReceive: ${_latLngList.value}")
                                            // mark parent as active when new lat long added to list
                                            updateParentActiveInactiveStatus(
                                                userId.value.toString(),
                                                Constants.ACTIVE_PARENT
                                            )
                                            Log.d("ParentActiveInactive", "Parent Status - Updated to Active")
                                        } else {
                                            Log.d("DEBUG", "Duplicate latLng skipped: $latLng")
                                            // Do NOT reset _bearing here — preserve previous
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

                                    // Apply pre-checks here to avoid unnecessary processing
                                    if (lat == 0.0 || lng == 0.0) return@launch

                                    // Avoid duplicates: compare with last visible or localLatLng
                                    val lastLatLng = _localLatLngList.value.lastOrNull()
                                    val isDuplicate = lastLatLng?.let {
                                        it.latitude == lat && it.longitude == lng
                                    } ?: false

                                    if (isDuplicate) {
                                        Log.d(
                                            "Broadcast",
                                            "Duplicate location, skipping: $lat, $lng"
                                        )
                                        return@launch
                                    }

                                    // Finally, send to server
                                    sendCurrentLocationToServer(location, speed)
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


    // This avoids sudden backward turns unless the user is truly reversing
    fun isBearingAcceptable(oldBearing: Float, newBearing: Float): Boolean {
        val diff = abs(oldBearing - newBearing) % 360
        val angleDiff = if (diff > 180) 360 - diff else diff
        return angleDiff < 90 // Accept only if change is < 90 degrees
    }

    /*    Your ViewModel already filters for:

        Kalman filtering

        Speed

        Accuracy

        Distance

        Duplicate locations

        Zig-zag detection

        Bearing smoothing

        Still detection

        Server throttling*/

    private var isFirstLocationSent = false // Add this at the top (outside function)

    private suspend fun sendCurrentLocationToServer(location: Location, speed: Double?) {
        if (userRole.value != "driver") return
        if (!isLocationPermissionGranted(context)) return

        try {
            // Apply Kalman Filter
            val filteredLatLng = kalmanFilter.process(
                location.latitude,
                location.longitude,
                location.accuracy,
                System.currentTimeMillis()
            )

            val filteredLocation = Location("").apply {
                latitude = filteredLatLng.latitude
                longitude = filteredLatLng.longitude
            }

            // Skip filters for the very first location
            if (isFirstLocationSent) {
                // Accuracy Check
                if (location.accuracy > 15f) {
                    Log.d("TAG", "Low accuracy: ${location.accuracy}, skipping.")
                    return
                }

                // Speed Check
                if (location.hasSpeed() && location.speed < 0.5f) {
                    Log.d("TAG", "Speed < 0.5m/s (${location.speed}), skipping.")
                    return
                }
                // Distance Check
                val lastLatLng = _visiblePolylinePath.value.lastOrNull()
                if (lastLatLng != null) {
                    val lastLocation = Location("").apply {
                        latitude = lastLatLng.latitude
                        longitude = lastLatLng.longitude
                    }

                    val distance = lastLocation.distanceTo(filteredLocation)
                    if (distance < 8f) {
                        Log.d("TAG", "Moved <$distance m, skipping.")
                        return
                    }
                }
            }

            // Log raw and filtered
            Log.d(
                "LOCATION_DEBUG", """
            Raw: (${location.latitude}, ${location.longitude})
            Filtered: (${filteredLatLng.latitude}, ${filteredLatLng.longitude})
            Accuracy: ${location.accuracy}
            Speed: ${location.speed}
        """.trimIndent()
            )

            // Send to Server
            val timestamp = getCurrentTimestamp()
            val address = getFullAddress(context, filteredLatLng.latitude, filteredLatLng.longitude)
            val formattedSpeed = String.format("%.2f", speed)
            driverId?.let { id ->
                val request = SendLatLongRequest(
                    id = id,
                    latitude = filteredLatLng.latitude.toString(),
                    longitude = filteredLatLng.longitude.toString(),
                    start_time = timestamp,
                    location = address ?: "Unknown",
                    lat_status = Constants.ACTIVE_TRACKING,
                    speed = formattedSpeed
                )
                repository.sendLatLong(request)
            }

            // Mark first location sent
            isFirstLocationSent = true

            // Update full path
            val updatedPath = _visiblePolylinePath.value.toMutableList()
            if (updatedPath.isEmpty() || updatedPath.last() != filteredLatLng) {
                updatedPath.add(filteredLatLng)
                _visiblePolylinePath.value = updatedPath
            }

            updateLocationAndBearing(filteredLatLng)

//            // Update last two points for bearing
//            val recentPoints = _localLatLngList.value.toMutableList()
//            if (recentPoints.isEmpty() || recentPoints.last() != filteredLatLng) {
//                if (recentPoints.size >= 2) recentPoints.removeFirst()
//                recentPoints.add(filteredLatLng)
//                _localLatLngList.value = recentPoints
//            }
//
//            // Bearing calculation
//            if (recentPoints.size == 2) {
//                val point1 = recentPoints[0]
//                val point2 = recentPoints[1]
//
//                val loc1 = Location("").apply {
//                    latitude = point1.latitude
//                    longitude = point1.longitude
//                }
//                val loc2 = Location("").apply {
//                    latitude = point2.latitude
//                    longitude = point2.longitude
//                }
//
//                val distance = loc1.distanceTo(loc2)
//                val newBearing = calculateBearing(point1, point2)
//
//                val isValidDirection =
//                    lastValidBearing == null || isBearingAcceptable(lastValidBearing!!, newBearing)
//
//                if (distance > 8f && isValidDirection) {
//                    _bearing.value = newBearing
//                    lastValidBearing = newBearing
//                    Log.d("TAG", "Updated bearing = $newBearing (Distance = $distance m)")
//                } else {
//                    Log.d(
//                        "TAG",
//                        "Skipped bearing: $newBearing (Distance = $distance, Last = $lastValidBearing)"
//                    )
//                }
//            }
        } catch (e: Exception) {
            Log.e("TAG", "Exception: ${e.localizedMessage}", e)
        }
    }

    fun updateLocationAndBearing(filteredLatLng: LatLng) {
        // Update last two points for bearing
        val recentPoints = _localLatLngList.value.toMutableList()

        // Add new point only if it's not duplicate
        if (recentPoints.lastOrNull() != filteredLatLng) {
            if (recentPoints.size >= MAX_RECENT_POINTS) {
                recentPoints.removeAt(0)
            }
            recentPoints.add(filteredLatLng)
            _localLatLngList.value = recentPoints
        }

        // Bearing calculation
        // Only update bearing if we have at least 2 points
        if (recentPoints.size >= 2) {
            val point1 = recentPoints[recentPoints.size - 2]
            val point2 = recentPoints[recentPoints.size - 1]

            val loc1 = Location("").apply {
                latitude = point1.latitude
                longitude = point1.longitude
            }

            val loc2 = Location("").apply {
                latitude = point2.latitude
                longitude = point2.longitude
            }

            val distance = loc1.distanceTo(loc2)
            val newBearing = calculateBearing(point1, point2)

            val now = System.currentTimeMillis()
            val timeSinceLast = now - lastBearingUpdateTime

            val angleDiff = lastValidBearing?.let {
                val diff = abs(it - newBearing) % 360
                if (diff > 180) 360 - diff else diff
            } ?: 0f

            val isValidDirection =
                (angleDiff < if (distance > 30f) 135 else 90) || timeSinceLast > 8000

            if (distance > 8f && isValidDirection) {
                _bearing.value = newBearing
                lastValidBearing = newBearing
                lastBearingUpdateTime = now
                Log.d("TAG", "Updated bearing = $newBearing (Distance = $distance m)")
            } else {
                Log.d(
                    "TAG",
                    "Skipped bearing: $newBearing (Distance = $distance m, AngleDiff = $angleDiff°)"
                )
            }
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

    // ─── Stop-tracking call ──────────────────────────────────────────────────────
    fun stopDriverTracking(id: String, status: String) {
        viewModelScope.launch {
            _stopTrackingState.value = Resource.Loading()
            try {
                val response = repository.stopTracking(id, status)
                _stopTrackingState.value = Resource.Success(response)

                /* -----------------------------------------------------------------
                 * Reset location-tracking flags only when the stop really succeeded.
                 * Two typical ways to decide that:
                 *   1. Caller passed a truthy status string ("true", "inactive", …)
                 *   2. Server response itself says “success == true”
                 * Adapt the condition to whatever your backend returns.
                 * ----------------------------------------------------------------- */
                val serverConfirmed = response.status   // if your DTO has it
                val callerSaysStop = status.equals("true", ignoreCase = true)

                if (serverConfirmed || callerSaysStop) {
                    isFirstLocationSent = false      // ⭐  <── reset here
                    lastValidBearing = null          // (optional) clear bearing cache
                    _visiblePolylinePath.value = emptyList()     // (optional) clear UI path
                    _localLatLngList.value = emptyList()         // (optional) clear bearing list
                    _bearing.value = 0f
                    Log.d("TAG", "Tracking stopped → runtime state cleared.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _stopTrackingState.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun clearParentRoute() {
        _visiblePolylinePathParent.value = emptyList<LatLng>()
        _latLngList.value = emptyList<LatLng>()
    }

    private val _parentActInActStatus =
        MutableStateFlow<Resource<ParentActiveInactiveResponse>>(Resource.Idle())
    val parentActInActStatus: StateFlow<Resource<ParentActiveInactiveResponse>> =
        _parentActInActStatus
    fun updateParentActiveInactiveStatus(parentId: String, status: String) {
        viewModelScope.launch {
            try {
                val response = repository.parentActiveInactiveStatus(parentId, status)
                if (response.status == true) {
                    _parentActInActStatus.value = Resource.Success(response)
                } else {
                    _parentActInActStatus.value =
                        Resource.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: Exception) {
                _parentActInActStatus.value =
                    Resource.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

}
