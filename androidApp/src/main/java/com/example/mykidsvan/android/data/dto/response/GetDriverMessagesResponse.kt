package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetDriverMessagesResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<DriverMessage> = arrayListOf()
)

data class DriverMessage(
    @SerializedName("id") var id: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("vehicles_id") var vehicleId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("parents") var parents: ArrayList<Parents> = arrayListOf()
)

data class Parents(
    @SerializedName("parent_name") var parentName: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null
)