package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class WithdrawRequestStatus(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<RequestStatus> = arrayListOf()
)

data class RequestStatus(
    @SerializedName("id") var id: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("withdraw_amount") var withdrawAmount: String? = null,
    @SerializedName("account_name") var accountName: String? = null,
    @SerializedName("ifsc_code") var ifscCode: String? = null,
    @SerializedName("account_number") var accountNumber: String? = null,
    @SerializedName("upi_id") var upiId: String? = null,
    @SerializedName("status") var status: String? = null
)