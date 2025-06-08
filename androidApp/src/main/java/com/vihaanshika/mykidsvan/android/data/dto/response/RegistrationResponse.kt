package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("districts") var districts: ArrayList<District> = arrayListOf(),
    @SerializedName("talukas") var talukas: ArrayList<Taluka> = arrayListOf()    // Assuming empty list; similar to districts
)
