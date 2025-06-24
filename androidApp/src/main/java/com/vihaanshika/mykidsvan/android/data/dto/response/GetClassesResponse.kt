package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetClassesResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<ClassDetails> = arrayListOf()
)

data class ClassDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("class_name") var className: String? = null
)