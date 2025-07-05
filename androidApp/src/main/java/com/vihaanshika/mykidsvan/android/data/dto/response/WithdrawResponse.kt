package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName
import com.vihaanshika.mykidsvan.android.data.dto.request.WithdrawRequest

data class WithdrawResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: WithdrawRequest? = WithdrawRequest()
)