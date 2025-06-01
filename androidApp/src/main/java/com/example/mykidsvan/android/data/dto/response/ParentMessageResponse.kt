package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class ParentMessageResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("sent_data") var sentData: SentDataP? = SentDataP()
)

data class SentDataP(
    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)