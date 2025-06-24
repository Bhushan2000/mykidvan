package com.vihaanshika.mykidsvan.android.ui.tracking

import com.google.android.gms.maps.model.LatLng
// Reduce GPS Noise
class SimpleKalmanLatLong(
    private val qMetresPerSecond: Float // Smoothness factor: try 3.0f
) {
    private var timestampMs: Long = 0
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var variance: Float = -1f

    fun process(lat_measurement: Double, lng_measurement: Double, accuracy: Float, timestamp: Long): LatLng {
        if (variance < 0) {
            this.lat = lat_measurement
            this.lng = lng_measurement
            this.variance = accuracy * accuracy
            this.timestampMs = timestamp
            return LatLng(lat, lng)
        }

        val timeDelta = (timestamp - timestampMs) / 1000f
        if (timeDelta > 0) {
            variance += timeDelta * qMetresPerSecond * qMetresPerSecond
            timestampMs = timestamp
        }

        val k = variance / (variance + accuracy * accuracy)
        lat += k * (lat_measurement - lat)
        lng += k * (lng_measurement - lng)
        variance *= (1 - k)

        return LatLng(lat, lng)
    }
}
