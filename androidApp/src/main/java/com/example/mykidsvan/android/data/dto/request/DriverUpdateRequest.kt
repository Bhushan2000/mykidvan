package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class DriverUpdateRequest(
    @SerializedName("id") var id: String? = null,
    @SerializedName("driver_name") var driverName: String? = null,
    @SerializedName("number") var number: String? = null,
    @SerializedName("vehicle_number") var vehicleNumber: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("taluka") var taluka: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("school_serviced") var schoolServiced: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null
)