package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class AllSchoolResponse(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<School> = arrayListOf()

)

data class School(

    @SerializedName("id") var id: String? = null,
    @SerializedName("school_name") var schoolName: String? = null,
    @SerializedName("contact_number") var contactNumber: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("taluka") var taluka: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("school_address") var schoolAddress: String? = null

)