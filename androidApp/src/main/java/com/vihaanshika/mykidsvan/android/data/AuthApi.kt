package com.vihaanshika.mykidsvan.android.data

import com.vihaanshika.mykidsvan.android.data.dto.request.AssignRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.CouponValidationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.DriverUpdateRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.OtpRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.OtpVerificationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentActiveInactiveRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentRegistrationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ParentUpdateRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.PaymentUpdateRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ReferByRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.ReferByResponse
import com.vihaanshika.mykidsvan.android.data.dto.request.RegisterSchoolDriverRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.RegisterSchoolParentRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendAssignRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.vihaanshika.mykidsvan.android.data.dto.request.StopTrackingRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.UpdateAssignRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.UpdatePasswordRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.UpdateTokenRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.WithdrawRequest
import com.vihaanshika.mykidsvan.android.data.dto.response.AllSchoolResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.AssignRequestAcpRejResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.AssignedResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionParentResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.CommissionResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.CouponValidationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DistrictsResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DocumentUploadResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverByMobResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverRequestResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.DriverResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetClassesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.LoginResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.MessageResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentActiveInactiveResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentMessageResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentsResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ProfileUpdateResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RazorpayOrderCreationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RegisterSchoolResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RegistrationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.SchoolRegistrationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.StatesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.StopTrackingResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.TalukasResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdatePasswordResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdatePaymentResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdateTokenResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.VehiclePhotosResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.WithdrawRequestStatus
import com.vihaanshika.mykidsvan.android.data.dto.response.WithdrawResponse
import com.vihaanshika.mykidsvan.android.utils.APIEndpoints
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Data Layer: AuthService.kt
interface AuthApi {

    @FormUrlEncoded
    @POST(APIEndpoints.LOGIN)
    suspend fun login(@FieldMap request: Map<String, String>): LoginResponse

    @POST(APIEndpoints.REGISTER_DRIVER)
    suspend fun registerDriver(@Body request: Map<String, String>): RegistrationResponse

    @POST(APIEndpoints.REGISTER_PARENT)
    suspend fun registerParent(@Body request: ParentRegistrationRequest): RegistrationResponse

    @GET(APIEndpoints.GET_STATE)
    suspend fun getState(): StatesResponse

    @GET(APIEndpoints.GET_DISTRICT)
    suspend fun getDistrict(@Path(APIEndpoints.PATH_STATE_ID) stateId: String): DistrictsResponse

    @GET(APIEndpoints.GET_TALUKA)
    suspend fun getTaluka(
        @Path(APIEndpoints.PATH_STATE_ID) stateId: String,
        @Path(APIEndpoints.PATH_DISTRICT_ID) districtId: String
    ): TalukasResponse

    @GET(APIEndpoints.GET_SCHOOLS)
    suspend fun getSchools(
        @Path(APIEndpoints.PATH_STATE_ID) stateId: String,
        @Path(APIEndpoints.PATH_DISTRICT_ID) districtId: String,
        @Path(APIEndpoints.PATH_TALUKA_ID) talukaId: String
    ): AllSchoolResponse

    @GET(APIEndpoints.GET_PARENTS)
    suspend fun getParents(@Path(APIEndpoints.PATH_SCHOOL_ID) schoolId: String): ParentsResponse

    @GET(APIEndpoints.GET_DRIVERS)
    suspend fun getDriver(@Path(APIEndpoints.PATH_SCHOOL_ID) schoolId: String): DriverResponse

    @POST(APIEndpoints.SEND_OTP)
    suspend fun sendOtp(@Body request: OtpRequest): OtpResponse

    @POST(APIEndpoints.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): OtpVerificationResponse

    @PUT(APIEndpoints.UPDATE_PASSWORD)
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): UpdatePasswordResponse

    @POST(APIEndpoints.REGISTER_SCHOOL)
    suspend fun registerSchools(@Body request: SchoolRegistrationRequest): SchoolRegistrationResponse

    @GET(APIEndpoints.GET_PARENT_PROFILE)
    suspend fun getParentProfile(@Path(APIEndpoints.PATH_PARENT_ID) parentId: String): ParentsResponse

    @GET(APIEndpoints.GET_DRIVER_PROFILE)
    suspend fun getDriverProfile(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): DriverResponse

    @PUT(APIEndpoints.ASSIGN_VEHICLE)
    suspend fun assignedVehicle(@Body request: AssignRequest): AssignedResponse

    @PUT(APIEndpoints.ASSIGN_STUDENT)
    suspend fun assignedStudent(@Body request: AssignRequest): AssignedResponse

    @GET(APIEndpoints.GET_ALL_SCHOOLS)
    suspend fun getAllSchools(): AllSchoolResponse

    @GET(APIEndpoints.GET_DRIVER_BY_MOBILE)
    suspend fun getDriverByMobNo(@Path(APIEndpoints.PATH_MOBILE_NO) mobileNo: String): DriverByMobResponse

    @PUT(APIEndpoints.SEND_LAT_LONG)
    suspend fun sendLatLong(@Body request: SendLatLongRequest): SendLatLongResponse

    @PUT(APIEndpoints.STOP_TRACKING)
    suspend fun stopTracking(@Body request: StopTrackingRequest): StopTrackingResponse

    @GET(APIEndpoints.GET_LAT_LONG)
    suspend fun getLatLong(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): GetLatLongResponse

    @PUT(APIEndpoints.UPDATE_ASSIGN_REQUEST)
    suspend fun updateAssignRequest(@Body request: UpdateAssignRequest): AssignRequestAcpRejResponse

    @POST(APIEndpoints.SEND_ASSIGN_REQUEST)
    suspend fun sendAssignRequest(@Body request: SendAssignRequest): SendRequestToDriverResponse

    @GET(APIEndpoints.GET_DRIVER_REQUESTS)
    suspend fun getDriverRequests(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): DriverRequestResponse

    @PUT(APIEndpoints.UPDATE_PROFILE_PARENT)
    suspend fun updateProfileParent(@Body request: ParentUpdateRequest): ProfileUpdateResponse

    @PUT(APIEndpoints.UPDATE_PROFILE_DRIVER)
    suspend fun updateProfileDriver(@Body request: DriverUpdateRequest): ProfileUpdateResponse

    @PUT(APIEndpoints.UPDATE_VEHICLE_PHOTOS)
    suspend fun updateVehiclePhotos(@Body request: UpdateVehicleImageRequest): ProfileUpdateResponse

    @PUT(APIEndpoints.UPDATE_PAYMENT)
    suspend fun updatePayment(@Body request: PaymentUpdateRequest): UpdatePaymentResponse

    @GET(APIEndpoints.CREATE_ORDER)
    suspend fun getOrderId(): RazorpayOrderCreationResponse

    @GET(APIEndpoints.GET_VEHICLE_PHOTOS)
    suspend fun getVehiclePhotos(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): VehiclePhotosResponse

    @POST(APIEndpoints.DRIVER_SCHOOL_ON_REGISTER)
    suspend fun driverSchoolOnRegister(@Body request: RegisterSchoolDriverRequest): RegisterSchoolResponse

    @POST(APIEndpoints.PARENT_SCHOOL_ON_REGISTER)
    suspend fun parentSchoolOnRegister(@Body request: RegisterSchoolParentRequest): RegisterSchoolResponse

    @PUT(APIEndpoints.UPLOAD_DOCUMENTS)
    suspend fun uploadDocumentsToDatabase(@Body request: Map<String, String>): DocumentUploadResponse

    @POST(APIEndpoints.CHECK_REFER_CODE)
    suspend fun checkReferCode(@Body request: ReferByRequest): ReferByResponse

    @GET(APIEndpoints.GET_DRIVER_COMMISSION)
    suspend fun getCommission(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): CommissionResponse

    @GET(APIEndpoints.GET_PARENT_COMMISSION)
    suspend fun getCommissionParent(@Path(APIEndpoints.PATH_PARENT_ID) parentId: String): CommissionParentResponse

    @GET(APIEndpoints.GET_ALL_PARENTS)
    suspend fun getAlParents(): ParentsResponse

    @POST(APIEndpoints.SEND_MESSAGE_TO_DRIVER_FROM_PARENT)
    suspend fun sendMessageToDriverFromParent(@Body request: SendMessageRequest): MessageResponse

    @POST(APIEndpoints.SEND_MESSAGE_TO_PARENT_FROM_DRIVER)
    suspend fun sendMessageToParentFromDriver(@Body request: SendMessageRequest): MessageResponse

    @GET(APIEndpoints.GET_PARENT_MESSAGES)
    suspend fun getParentMessage(@Path(APIEndpoints.PATH_PARENT_ID) parentId: String): GetParentMessagesResponse

    @GET(APIEndpoints.GET_DRIVER_MESSAGES)
    suspend fun getDriverMessage(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): GetDriverMessagesResponse

    @GET(APIEndpoints.GET_DRIVER_ALL_MESSAGES)
    suspend fun getDriverAllMessage(@Path(APIEndpoints.PATH_DRIVER_ID) driverId: String): GetDriverAllMessagesResponse

    @PUT(APIEndpoints.UPDATE_DEVICE_TOKEN)
    suspend fun updateDeviceToken(@Body request: UpdateTokenRequest): UpdateTokenResponse

    @GET(APIEndpoints.GET_ClASSES)
    suspend fun getClasses(): GetClassesResponse

    @POST(APIEndpoints.WITHDRAW_REQUEST)
    suspend fun withdrawCommission(@Body request: WithdrawRequest): WithdrawResponse

    @GET(APIEndpoints.WITHDRAW_REQUEST_STATUS)
    suspend fun withdrawCommissionRequests(
        @Path(APIEndpoints.PATH_USER_ID) id: String,
        @Path(APIEndpoints.PATH_ROLE) role: String
    ): WithdrawRequestStatus

    @POST(APIEndpoints.PARENT_STATUS)
    suspend fun parentActiveInactiveStatus(
        @Body request: ParentActiveInactiveRequest
    ): ParentActiveInactiveResponse

    @PUT(APIEndpoints.COUPON_VALIDATION)
    suspend fun couponValidation(
        @Body request : CouponValidationRequest
    ) : CouponValidationResponse
}