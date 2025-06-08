package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class VehiclePhotosResponse(
    @SerializedName("status" ) var status : Boolean?          = null,
    @SerializedName("photos" ) var photos : ArrayList<String> = arrayListOf()
)
