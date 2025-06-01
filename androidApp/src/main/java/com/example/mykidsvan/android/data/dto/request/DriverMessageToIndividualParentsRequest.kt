package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class DriverMessageToIndividualParentsRequest(
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("message") var message: String? = null
)
