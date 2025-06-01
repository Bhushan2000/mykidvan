package com.example.maptracking


import com.example.mykidsvan.android.data.AuthApi
import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.example.mykidsvan.android.ui.tracking.LatLngRepository

class LatLngRepositoryImpl(private val api: AuthApi) : LatLngRepository {

    override suspend fun sendLatLong(request: SendLatLongRequest): SendLatLongResponse =
        api.sendLatLong(request)

    override suspend fun getLatLong(driverId: String): GetLatLongResponse =
        api.getLatLong(driverId)
}
