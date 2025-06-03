package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class DriverMessageIndividualPMsgResponse (

  @SerializedName("status"    ) var status   : Boolean?  = null,
  @SerializedName("message"   ) var message  : String?   = null,
  @SerializedName("sent_data" ) var sentData : SentData? = SentData()

)
