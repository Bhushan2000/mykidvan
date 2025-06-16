package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetLatLongResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<DriverDetails> = arrayListOf()
)

data class DriverDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("driver_type") var driverType: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("timer") var timer: String? = null,
    @SerializedName("lat_status") var lat_status: String? = null,
    @SerializedName("location") var location: String? = null,
)