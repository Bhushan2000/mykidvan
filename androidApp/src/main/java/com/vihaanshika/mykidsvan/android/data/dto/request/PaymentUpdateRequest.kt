package com.vihaanshika.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class PaymentUpdateRequest(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("payment_id") var paymentId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("payment_status") var paymentStatus: String? = null,
    @SerializedName("expire_date") var expireDate: String? = null,
    @SerializedName("payment_date") var paymentDate: String? = null,
    @SerializedName("assign_status") var assignStatus: String? = null,
    @SerializedName("assign_date") var assignDate: String? = null,
    @SerializedName("signature") var signature: String? = null,
    @SerializedName("order_id") var orderId: String? = null
)