package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateVehicleImageRequest(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("photo_of_vehicle") var photoOfVehicle: ArrayList<String> = arrayListOf()

)