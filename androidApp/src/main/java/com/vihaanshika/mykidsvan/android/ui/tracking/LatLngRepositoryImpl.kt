package com.example.maptracking


import com.vihaanshika.mykidsvan.android.data.AuthApi
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentActiveInactiveRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.StopTrackingRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentActiveInactiveResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.StopTrackingResponse
import com.vihaanshika.mykidsvan.android.ui.tracking.LatLngRepository

class LatLngRepositoryImpl(private val api: AuthApi) : LatLngRepository {

    override suspend fun sendLatLong(request: SendLatLongRequest): SendLatLongResponse =
        api.sendLatLong(request)

    override suspend fun getLatLong(driverId: String): GetLatLongResponse =
        api.getLatLong(driverId)

    override suspend fun stopTracking(id: String, status: String): StopTrackingResponse {
        val request  = StopTrackingRequest(id,status)
       return api.stopTracking(request)
    }

    override suspend fun parentActiveInactiveStatus(
        parentId: String,
        status: String
    ): ParentActiveInactiveResponse {
        val request = ParentActiveInactiveRequest(parentId,status)
        return api.parentActiveInactiveStatus(request)
    }
}
