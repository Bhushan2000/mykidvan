package com.vihaanshika.mykidsvan.android.utils

import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMessageAllPMsgResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverMessageIndividualPMsgResponse

sealed class DriverMessageResponseWrapper {
    data class AllParentsResponse(val response: DriverMessageAllPMsgResponse) : DriverMessageResponseWrapper()
    data class IndividualParentResponse(val response: DriverMessageIndividualPMsgResponse) : DriverMessageResponseWrapper()
}