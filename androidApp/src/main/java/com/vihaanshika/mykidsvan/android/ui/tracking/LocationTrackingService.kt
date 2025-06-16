package com.vihaanshika.mykidsvan.android.ui.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import com.vihaanshika.mykidsvan.android.utils.LocationFetcher
import com.vihaanshika.mykidsvan.android.utils.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

//
class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repository: LatLngRepository by inject()
    private val userPreferences: UserPreferences by inject()
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole
    private var trackingJob: Job? = null

    var driverId: String? = null// Your default/fallback
    private val _trackingStatus = MutableStateFlow<String?>(null)
    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "onCreate called")
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        serviceScope.launch {
            combine(
                userPreferences.userRole,
                userPreferences.assignVehicleIdFlow,
                userPreferences.trackingStatusFlow // ← from DataStore (or use your StateFlow)
            ) { role, id, trackingStatus ->
                Triple(role, id, trackingStatus)
            }.distinctUntilChanged()
                .collect { (role, id, trackingStatus) ->
                    _userRole.value = role

                    // Cancel any old fetch job before starting new one
                    trackingJob?.cancel()

                    if (role != Constants.USER_DRIVER && id != null) {
                        driverId = id
                        Log.d("LocationService", "Starting periodic fetch...")

                        trackingJob = launch {
                            while (isActive) {
                                fetchLatLngFromServer()
                                delay(10_000L)
                            }
                        }
                    } else {
                        Log.d(
                            "LocationService",
                            "Tracking not started. Role=$role ID=$id Status=$trackingStatus"
                        )
                    }
                }
        }

    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L // Every 10 seconds
        ).setMinUpdateIntervalMillis(5_000L) // Minimum 5 seconds between updates
            .setMaxUpdateDelayMillis(15_000L) // Max delay if batching
            .build()

        // ✅ Permission check
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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return

            // 👉 Send broadcast, save to DB, or call repository
            // Log.d("LocationService", "Location: ${location.latitude}, ${location.longitude}")

            // Example: Send broadcast to ViewModel
            val intent = Intent(Constants.LOCATION_BROADCAST_ACTION).apply {
                putExtra("latitude", location.latitude)
                putExtra("longitude", location.longitude)
            }
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService", "onDestroy called")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel() // 👈 Cancel coroutine on service destruction

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel_id",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for tracking user location"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // ✅ Notification setup
    private fun createNotification(): Notification {
        val channelId = "location_channel_id"
        // Intent to open MapActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
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
            .setContentIntent(pendingIntent) // 👈 PendingIntent added
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 101
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
                    // ✅ Send broadcast
                    val intent = Intent(Constants.SERVER_LOCATION_BROADCAST_ACTION).apply {
                        putExtra("latitude", latLng.latitude)
                        putExtra("longitude", latLng.longitude)
                    }
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                    Log.d(
                        "LocationService",
                        "Broadcast send for Location: ${latLng.latitude}, ${latLng.longitude}"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification()) // 👈 move it here
        return START_STICKY
    }
}
