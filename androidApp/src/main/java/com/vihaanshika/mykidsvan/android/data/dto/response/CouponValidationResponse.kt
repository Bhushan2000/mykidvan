package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CouponValidationResponse(

    @SerializedName("status") var status: Boolean,
    @SerializedName("message") var message: String? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("receipt_id") var receiptId: String? = null,
    @SerializedName("pay_amount") var payAmount: String? = null

)