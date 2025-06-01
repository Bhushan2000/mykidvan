package com.example.mykidsvan.android.ui.tracking

import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse

interface LatLngRepository {
    suspend fun sendLatLong(request: SendLatLongRequest): SendLatLongResponse
    suspend fun getLatLong(driverId: String): GetLatLongResponse
}