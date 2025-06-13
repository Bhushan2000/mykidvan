package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class RazorpayOrderCreationResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("receipt_id") var receiptId: String? = null,
    @SerializedName("amount_paise") var amountPaise: Int? = null,
    @SerializedName("amount_rupees") var amountRupees: String? = null
)