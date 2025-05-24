package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class RegisterSchoolResponse(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("school_id") var schoolId: String? = null

)