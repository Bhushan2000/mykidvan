package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CommissionResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: Data = Data()
)

data class Data(
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("total_withdrawn") var totalWithdrawn: String? = null,
    @SerializedName("remaining") var remaining: String? = null,
)