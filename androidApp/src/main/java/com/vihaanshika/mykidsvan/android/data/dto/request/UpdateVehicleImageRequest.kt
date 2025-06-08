package com.vihaanshika.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateVehicleImageRequest(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("photo_of_vehicle") var photoOfVehicle: PhotoOfVehicle? = PhotoOfVehicle()
)

data class PhotoOfVehicle(
    @SerializedName("0") var frontImage: String? = null,
    @SerializedName("1") var backImage: String? = null,
    @SerializedName("2") var insideImage: String? = null,
    @SerializedName("3") var otherImage: String? = null
)