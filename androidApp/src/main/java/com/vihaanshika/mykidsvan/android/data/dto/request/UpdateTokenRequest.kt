package com.vihaanshika.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateTokenRequest(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null
)