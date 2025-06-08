package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    val data: JsonElement // Handle polymorphically
)
