package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CommissionParentResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<Commission> = arrayListOf()
)
data class Commission(
    @SerializedName("commission") var amount: String? = null
)