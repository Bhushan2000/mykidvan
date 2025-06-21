package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class AssignRequestAcpRejResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("topic_updated") var topicUpdated: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null,
    @SerializedName("fcm_result") var fcmResult: FcmResult? = FcmResult(),
    @SerializedName("fcm_error") var fcmError: String? = null
)
data class FcmResult (
    @SerializedName("name" ) var name : String? = null
)