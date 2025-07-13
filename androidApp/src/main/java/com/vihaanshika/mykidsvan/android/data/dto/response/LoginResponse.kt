package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") var status: Boolean,
    @SerializedName("message") var message: String? = null,
    @SerializedName("role") var userRole: String? = null,
    @SerializedName("school_image") var school_image: String? = null,
    @SerializedName("pay_amount") var pay_amount: String? = null,
    val data: JsonElement // Handle polymorphically
)
