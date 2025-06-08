package com.vihaanshika.mykidsvan.android.data.dto.response

import com.vihaanshika.mykidsvan.android.data.dto.request.SentData
import com.google.gson.annotations.SerializedName

// all
data class DriverMessageAllPMsgResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("sent_data") var sentData: ArrayList<SentData> = arrayListOf()

)