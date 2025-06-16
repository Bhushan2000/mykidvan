package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class SendLatLongResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("updated_data") var updatedData: UpdatedData? = UpdatedData()
)

data class UpdatedData(
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("lat_status") var latStatus: String? = null
)