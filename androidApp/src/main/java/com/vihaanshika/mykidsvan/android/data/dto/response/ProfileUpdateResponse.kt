package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileUpdateResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null
)
