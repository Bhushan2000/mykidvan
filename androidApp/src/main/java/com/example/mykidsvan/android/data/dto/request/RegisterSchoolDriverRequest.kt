package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterSchoolDriverRequest(

    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("school_id") var schoolId: String? = null,
    @SerializedName("school_name") var schoolName: String? = null,
    @SerializedName("contact_number") var contactNumber: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("taluka") var taluka: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("school_address") var schoolAddress: String? = null

)