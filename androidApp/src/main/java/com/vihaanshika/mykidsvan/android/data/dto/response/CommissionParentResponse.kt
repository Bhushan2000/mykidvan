package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CommissionParentResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: Commission = Commission()
)
data class Commission(
    @SerializedName("commission") var commission: String? = null,
    @SerializedName("total_withdrawn") var totalWithdrawn: String? = null,
    @SerializedName("remaining") var remaining: String? = null,
)