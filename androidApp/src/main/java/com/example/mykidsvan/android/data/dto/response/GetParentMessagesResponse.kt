package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetParentMessagesResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Message> = arrayListOf()
)

data class Message(
    @SerializedName("id") var id: String? = null,
    @SerializedName("search_specific") var searchSpecific: String? = null,
    @SerializedName("parent_id") val parent_id: String? = null,
    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("vehicles") var drivers: ArrayList<Drivers> = arrayListOf()
)

data class Drivers(
    @SerializedName("driver_name") var driverName: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null
)