package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class DriverMessageToIndividualParentsRequest(
    @SerializedName("search_specific") var searchSpecific: String? = null, // individual // all
    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("message") var message: String? = null,

)
