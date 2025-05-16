package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class SendRequestToDriverResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("sent_data") var sentData: SentData? = SentData()
)

data class SentData(
    @SerializedName("vehicle_id") var vehicleId: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)