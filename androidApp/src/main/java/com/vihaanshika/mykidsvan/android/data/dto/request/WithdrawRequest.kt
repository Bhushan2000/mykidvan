package com.vihaanshika.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class WithdrawRequest(
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("withdraw_amount") var withdrawAmount: String? = null,
    @SerializedName("account_name") var accountName: String? = null,
    @SerializedName("ifsc_code") var ifscCode: String? = null,
    @SerializedName("account_number") var accountNumber: String? = null,
    @SerializedName("upi_id") var upiId: String? = null
)