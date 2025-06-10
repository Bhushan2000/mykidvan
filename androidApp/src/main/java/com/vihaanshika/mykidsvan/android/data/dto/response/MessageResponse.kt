package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("sent_data") var sentData: SentData? = SentData()
)

data class SentData(
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("search_specific") var searchSpecific: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)