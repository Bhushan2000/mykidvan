package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class DriverMessageToAllParentsRequest(
    @SerializedName("search_specific") var searchSpecific: String? = null, // individual // all
    @SerializedName("driver_id") var driverId: String? = null,
    @SerializedName("message") var message: String? = null
)
