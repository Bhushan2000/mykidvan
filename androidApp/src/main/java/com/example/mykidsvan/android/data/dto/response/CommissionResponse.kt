package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CommissionResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()
)

data class Data(
    @SerializedName("amount") var amount: String? = null
)