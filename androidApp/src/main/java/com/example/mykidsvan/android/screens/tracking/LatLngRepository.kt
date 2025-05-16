package com.example.maptracking


import com.example.mykidsvan.android.data.AuthApi
import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse

class LatLngRepository(private val api: AuthApi) {
    suspend fun sendLatLong(request: SendLatLongRequest): SendLatLongResponse =
        api.sendLatLong(request)

    suspend fun getLatLong(driverId: String): GetLatLongResponse =
        api.getLatLong(driverId)
}
