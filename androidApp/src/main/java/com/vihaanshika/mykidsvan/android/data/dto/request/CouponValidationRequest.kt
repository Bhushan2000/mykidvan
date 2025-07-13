package com.vihaanshika.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class CouponValidationRequest (

  @SerializedName("id"        ) var id       : String? = null,
  @SerializedName("coupon_id" ) var couponId : String? = null

)