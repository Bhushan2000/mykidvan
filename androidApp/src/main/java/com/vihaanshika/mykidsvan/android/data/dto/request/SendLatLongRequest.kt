package com.vihaanshika.mykidsvan.android.data.dto.request

data class SendLatLongRequest(
    val id: String,
    val latitude: String,
    val longitude: String,
    val start_time: String,
    val location: String,
    val lat_status: String
)
