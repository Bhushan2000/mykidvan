package com.example.mykidsvan.android.data

import com.example.mykidsvan.android.data.dto.request.AssignRequest
import com.example.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.example.mykidsvan.android.data.dto.response.DriverMessageResponse
import com.example.mykidsvan.android.data.dto.request.DriverUpdateRequest
import com.example.mykidsvan.android.data.dto.request.OtpRequest
import com.example.mykidsvan.android.data.dto.request.OtpVerificationRequest
import com.example.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.example.mykidsvan.android.data.dto.request.ParentRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.ParentUpdateRequest
import com.example.mykidsvan.android.data.dto.request.PaymentUpdateRequest
import com.example.mykidsvan.android.data.dto.request.ReferByRequest
import com.example.mykidsvan.android.data.dto.request.ReferByResponse
import com.example.mykidsvan.android.data.dto.request.RegisterSchoolDriverRequest
import com.example.mykidsvan.android.data.dto.request.RegisterSchoolParentRequest
import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.SendAssignRequest
import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.example.mykidsvan.android.data.dto.request.UpdateAssignRequest
import com.example.mykidsvan.android.data.dto.request.UpdatePasswordRequest
import com.example.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.example.mykidsvan.android.data.dto.response.AllSchoolResponse
import com.example.mykidsvan.android.data.dto.response.AssignedResponse
import com.example.mykidsvan.android.data.dto.response.CommissionResponse
import com.example.mykidsvan.android.data.dto.response.DistrictsResponse
import com.example.mykidsvan.android.data.dto.response.DocumentUploadResponse
import com.example.mykidsvan.android.data.dto.response.DriverByMobResponse
import com.example.mykidsvan.android.data.dto.response.DriverRequestResponse
import com.example.mykidsvan.android.data.dto.response.DriverResponse
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.LoginResponse
import com.example.mykidsvan.android.data.dto.response.OtpResponse
import com.example.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.example.mykidsvan.android.data.dto.response.ParentMessageResponse
import com.example.mykidsvan.android.data.dto.response.ParentsResponse
import com.example.mykidsvan.android.data.dto.response.ProfileUpdateResponse
import com.example.mykidsvan.android.data.dto.response.RegisterSchoolResponse
import com.example.mykidsvan.android.data.dto.response.RegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SchoolRegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.example.mykidsvan.android.data.dto.response.StatesResponse
import com.example.mykidsvan.android.data.dto.response.TalukasResponse
import com.example.mykidsvan.android.data.dto.response.UpdatePasswordResponse
import com.example.mykidsvan.android.data.dto.response.UpdatePaymentResponse
import com.example.mykidsvan.android.data.dto.response.VehiclePhotosResponse
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
    @POST("index.php/api/Logincontroller/login")
    suspend fun login(@FieldMap request: Map<String, String>): LoginResponse

    @POST("index.php/api/AccountsController/vehicles")
    suspend fun registerDriver(@Body request: Map<String, String>): RegistrationResponse

    @POST("index.php/api/AccountsController/parent")
    suspend fun registerParent(@Body request: ParentRegistrationRequest): RegistrationResponse

    @GET("index.php/api/AccountsController/get_state")
    suspend fun getState(): StatesResponse

    @GET("index.php/api/AccountsController/get_district/{state_id}")
    suspend fun getDistrict(@Path("state_id") state_id: String): DistrictsResponse

    @GET("index.php/api/AccountsController/get_taluka/{state_id}/{district_id}")
    suspend fun getTaluka(
        @Path("state_id") state_id: String,
        @Path("district_id") district_id: String
    ): TalukasResponse

    @GET("index.php/api/AccountsController/get_school/{state_id}/{district_id}/{taluka_id}")
    suspend fun getSchools(
        @Path("state_id") state_id: String,
        @Path("district_id") district_id: String,
        @Path("taluka_id") taluka_id: String
    ): AllSchoolResponse

    @GET("index.php/api/AccountsController/get_parent/{school_id}")
    suspend fun getParents(@Path("school_id") state_id: String): ParentsResponse

    @GET("index.php/api/AccountsController/get_driver/{school_id}")
    suspend fun getDriver(@Path("school_id") state_id: String): DriverResponse

    ////////////////////////////////////////////////////////////////////////////////////
    @POST("index.php/api/AccountsController/store_otp")
    suspend fun sendOtp(@Body request: OtpRequest): OtpResponse

    @POST("index.php/api/AccountsController/verify_otp")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): OtpVerificationResponse

    @PUT("index.php/api/AccountsController/update_password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): UpdatePasswordResponse

    @POST("index.php/api/AccountsController/school")
    suspend fun registerSchools(@Body request: SchoolRegistrationRequest): SchoolRegistrationResponse

    @GET("index.php/api/AccountsController/get_parentdetail/{parent_id}")
    suspend fun getParentProfile(@Path("parent_id") parent_id: String): ParentsResponse

    @GET("index.php/api/AccountsController/get_driverdetail/{driver_id}")
    suspend fun getDriverProfile(@Path("driver_id") driver_id: String): DriverResponse

    @PUT("index.php/api/AccountsController/update_vehiclesschool_id")
    suspend fun assignedVehicle(@Body request: AssignRequest): AssignedResponse

    @PUT("index.php/api/AccountsController/update_parentschool_id")
    suspend fun assignedStudent(@Body request: AssignRequest): AssignedResponse

    @GET("index.php/api/AccountsController/get_school_registeration")
    suspend fun getAllSchools(): AllSchoolResponse

    @GET("/index.php/api/AccountsController/get_driverbynumber/{mobile_no}")
    suspend fun getDriverByMobNo(@Path("mobile_no") driver_id: String): DriverByMobResponse

    // map api's
    @PUT("index.php/api/AccountsController/update_vehicles")
    suspend fun sendLatLong(@Body request: SendLatLongRequest): SendLatLongResponse

    @GET("index.php/api/AccountsController/get_vehicles/{driver_id}")
    suspend fun getLatLong(
        @Path("driver_id") driver_id: String
    ): GetLatLongResponse

    @PUT("index.php/api/AccountsController/vehicle_message")
    suspend fun updateAssignRequest(@Body request: UpdateAssignRequest): SendLatLongResponse

    @POST("index.php/api/AccountsController/vehicle_message")
    suspend fun sendAssignRequest(@Body request: SendAssignRequest): SendRequestToDriverResponse

    @GET("index.php/api/AccountsController/get_vehicle_message/{driver_id}")
    suspend fun getDriverRequests(
        @Path("driver_id") driver_id: String,
    ): DriverRequestResponse

    @PUT("index.php/api/AccountsController/update_parentdetail")
    suspend fun updateProfileParent(
        @Body request: ParentUpdateRequest
    ): ProfileUpdateResponse

    @PUT("index.php/api/AccountsController/update_driverdetail")
    suspend fun updateProfileDriver(
        @Body request: DriverUpdateRequest
    ): ProfileUpdateResponse

    @PUT("index.php/api/AccountsController/update_vehicle_photo")
    suspend fun updateVehiclePhotos(
        @Body request: UpdateVehicleImageRequest
    ): ProfileUpdateResponse

    @PUT("index.php/api/AccountsController/update_payment")
    suspend fun updatePayment(
        @Body request: PaymentUpdateRequest
    ): UpdatePaymentResponse

    @GET("index.php/api/AccountsController/get_vehicle_photos/{driver_id}")
    suspend fun getVehiclePhotos(
        @Path("driver_id") driver_id: String
    ): VehiclePhotosResponse

    @POST("index.php/api/AccountsController/school_vehicles")
    suspend fun driverSchoolOnRegister(
        @Body request: RegisterSchoolDriverRequest
    ): RegisterSchoolResponse

    @POST("index.php/api/AccountsController/school")
    suspend fun parentSchoolOnRegister(
        @Body request: RegisterSchoolParentRequest
    ): RegisterSchoolResponse

    @PUT("index.php/api/AccountsController/update_images")
    suspend fun uploadDocumentsToDatabase(@Body request: Map<String, String>): DocumentUploadResponse

    @POST("index.php/api/AccountsController/check_refer_by_status")
    suspend fun checkReferCode(
        @Body request: ReferByRequest
    ): ReferByResponse

    @GET("index.php/api/AccountsController/get_commision/{driver_id}")
    suspend fun getCommission(@Path("driver_id") driver_id: String): CommissionResponse

    @GET("index.php/api/AccountsController/get_all_parents")
    suspend fun getAlParents(): ParentsResponse

    @POST("index.php/api/AccountsController/parent_message")
    suspend fun sendMessageToDriverFromParent(@Body request: ParentMessageToDriverRequest): ParentMessageResponse

    @POST("index.php/api/AccountsController/driver_message") // all // individual
    suspend fun sendMessageToParentFromDriver(@Body request: DriverMessageToAllParentsRequest): DriverMessageResponse

}
