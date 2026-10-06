package com.vihaanshika.mykidsvan.android.ui.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.maptracking.LatLngViewModel
import com.vihaanshika.mykidsvan.android.R
import com.vihaanshika.mykidsvan.android.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.vihaanshika.mykidsvan.android.MainActivity
import com.vihaanshika.mykidsvan.android.utils.PlaceHolders
import com.vihaanshika.mykidsvan.android.utils.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//
class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository: LatLngRepository by inject()
    private val userPreferences: UserPreferences by inject()
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole
    private var trackingJob: Job? = null
    private var speedInKmh: Double? = null
    var driverId: String? = null// Your default/fallback
    var pollingStatus: Boolean? = null
    private var lastSentLocation: Location? = null
    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "onCreate called")
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        serviceScope.launch {
            combine(
                userPreferences.userRoleFlow,
                userPreferences.assignVehicleIdFlow,
            ) { role, id ->
                Pair(role, id)
            }.distinctUntilChanged()
                .collect { (role, id) ->
                    _userRole.value = role
                    driverId = id

                    // Cancel any old fetch job before starting new one
                    trackingJob?.cancel()

                    if (role != Constants.USER_DRIVER && id != null) {
                        Log.d("LocationService", "Starting periodic fetch...")
                        speedInKmh?.equals(0.0)
                        trackingJob = launch {
                            while (isActive && (pollingStatus == null || pollingStatus == true)) {
                                fetchLatLngFromServer()
                                delay(10_000L)
                                Log.d("TAG", "onCreate: polling status - $pollingStatus")
                            }
                        }
                    }
                }
        }

    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, // Use PRIORITY_HIGH_ACCURACY for GPS + WiFi + Cell towers
            5000L // Request update every 5 seconds
        ).apply {
            setMinUpdateIntervalMillis(2000L) // Don't get updates more than every 2 seconds
            setWaitForAccurateLocation(true)  // Wait for a precise fix
            setMaxUpdateDelayMillis(10000L)   // In case of batching
        }.build()

        // Permission check
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationService", "Missing location permissions")
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isRedundantLocation(newLoc: Location): Boolean {
        lastSentLocation?.let { lastLoc ->
            val distance = lastLoc.distanceTo(newLoc)
            val timeDiff = newLoc.time - lastLoc.time

            // If moved less than 5 meters and it's been less than 4 seconds, skip
            if (distance < 5 && timeDiff < 4000) {
                Log.d(
                    "LocationService",
                    "📍 Redundant location (Distance: $distance m, TimeDiff: $timeDiff ms), skipping broadcast."
                )
                return true
            }
        }
        lastSentLocation = newLoc
        return false
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            // 🔍 Avoid bad GPS
            if (location.accuracy > 50f) {
                Log.d("Service", "Too low accuracy: ${location.accuracy}, skipping broadcast.")
                return
            }

            if (location.speed < 0.5f) {
                Log.d("Service", "Speed < 0.5 m/s, likely idle or creeping. Skipping.")
                return
            }
            // 🌀 Filter redundant jitter
            if (isRedundantLocation(location)) { // isRedundantLocation() filters jitter
                Log.d("Service", "Redundant or similar to previous point. Skipping.")
                return // 🔥 Skip sending duplicate/noisy updates
            }
            speedInKmh = location.speed * 3.6  // Float in m/s -> Double in km/h
            Log.d("TAG", "onLocationResult: ${speedInKmh?.format(2)} km/h")

            // Example: Send broadcast to ViewModel
            val intent = Intent(Constants.LOCATION_BROADCAST_ACTION).apply {
                putExtra("latitude", location.latitude)
                putExtra("longitude", location.longitude)
                putExtra("speed", speedInKmh)
            }
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            // Update notification with latest speed
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationTrackingService", "onDestroy called")
        // Cancel location tracking job
        trackingJob?.cancel()
        // Cancel service scope to stop all coroutines
        serviceScope.cancel()
        // Stop fused location updates if needed
        fusedLocationClient.removeLocationUpdates(locationCallback)
        pollingStatus = false
        Log.d("LocationTrackingService", "All jobs and location updates cancelled")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.LOCATION_CHANNEL,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for tracking user location"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // Notification setup
    private fun createNotification(): Notification {
        val channelId = Constants.LOCATION_CHANNEL
        // Intent to open MapActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val speed = speedInKmh ?: 0.0
        val title = if ((speedInKmh ?: 0.0) < 1.0) {
            "Tracking Inactive"
        } else {
            "Tracking Active"
        }
        val speedText = if ((speedInKmh ?: 0.0) < 1.0) {
            PlaceHolders.MSG_TRACKING_NOT_STARTED
        } else {
            "Vehicle is moving at ${speedInKmh?.format(2)} km/h"
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(speedText)
            .setSmallIcon(R.drawable.baseline_location_on_24)
            .setColor(Color.BLUE)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationDriver(): Notification {
        val channelId = "location_channel_id"

        // Open MainActivity intent
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Exit action intent
        val stopIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = Constants.ACTION_STOP_TRACKING
        }

        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking Location")
            .setContentText("Location is being tracked in background")
            .setSmallIcon(R.drawable.baseline_location_on_24)
            .setColor(Color.BLUE)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Stop", stopPendingIntent) // 👉 Text-only "Stop" action
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 101
    }

    private suspend fun fetchLatLngFromServer() {
        try {
            val response = driverId?.let { repository.getLatLong(it) }
            val fetchedSpeed = response?.data?.firstOrNull()?.speed
            val trackingStatus = response?.data?.firstOrNull()?.lat_status
            if (trackingStatus?.equals(Constants.ACTIVE_TRACKING) == true) {
                speedInKmh = fetchedSpeed?.toDouble()
                pollingStatus = true
            } else {
                speedInKmh = 0.00
                pollingStatus = false
            }

            if (response?.status == true && response.data.isNotEmpty()) {
                val newLatLng = response.data.firstOrNull()?.let {
                    LatLng(
                        it.latitude?.toDoubleOrNull() ?: return,
                        it.longitude?.toDoubleOrNull() ?: return
                    )
                }

                newLatLng?.let { latLng ->
                    // Send broadcast
                    val intent = Intent(Constants.SERVER_LOCATION_BROADCAST_ACTION).apply {
                        putExtra("latitude", latLng.latitude)
                        putExtra("longitude", latLng.longitude)
                        putExtra("speed", fetchedSpeed?.toDouble())
                        putExtra("tracking_status", trackingStatus)
                    }
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
                // Update notification with latest speed
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, createNotification())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Constants.ACTION_STOP_TRACKING) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Show a fallback notification immediately to avoid crash
        startForeground(NOTIFICATION_ID, createStartupNotification())

        // Continue logic here...
        return START_STICKY
    }

    // 🔧 Extension function to format Double
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private fun createStartupNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.LOCATION_CHANNEL)
            .setContentTitle("Tracking Started...")
            .setContentText("Please wait while tracking starts")
            .setSmallIcon(R.drawable.baseline_location_on_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun restartPolling() {
        trackingJob?.cancel()
        trackingJob = serviceScope.launch {
            while (isActive && (pollingStatus == null || pollingStatus == true)) {
                fetchLatLngFromServer()
                delay(10_000L)
                Log.d("restartPolling", "Polling... still active")
            }
        }
    }

}
