package com.vihaanshika.mykidsvan.android.ui.tracking

import com.vihaanshika.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentActiveInactiveResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.StopTrackingResponse

interface LatLngRepository {
    suspend fun sendLatLong(request: SendLatLongRequest): SendLatLongResponse
    suspend fun getLatLong(driverId: String): GetLatLongResponse
    suspend fun stopTracking(id:String, status: String): StopTrackingResponse

    suspend fun parentActiveInactiveStatus(parentId:String, status: String) : ParentActiveInactiveResponse

}