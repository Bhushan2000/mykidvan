package com.vihaanshika.mykidsvan.android.data

import com.vihaanshika.mykidsvan.android.data.dto.request.ReferByResponse
import com.vihaanshika.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendMessageRequest
import com.vihaanshika.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
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
import com.vihaanshika.mykidsvan.android.data.dto.response.GetParentMessagesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.LoginResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.MessageResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentActiveInactiveResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentsResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.ProfileUpdateResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RazorpayOrderCreationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RegisterSchoolResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.RegistrationResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.SchoolRegistrationResponse

import com.vihaanshika.mykidsvan.android.data.dto.response.StatesResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.TalukasResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdatePasswordResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdatePaymentResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.UpdateTokenResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.VehiclePhotosResponse
import com.vihaanshika.mykidsvan.android.data.dto.response.WithdrawRequestStatus
import com.vihaanshika.mykidsvan.android.data.dto.response.WithdrawResponse

// Domain Layer: AuthRepository.kt
interface AuthRepository {

    suspend fun login(email: String, password: String, token: String,
                      app_version: String, os_version: String,
                      device_model: String,last_seen: String): LoginResponse

    suspend fun sendOtp(number: String, purpose: String): OtpResponse

    suspend fun verifyOtp(number: String, otp: String): OtpVerificationResponse

    suspend fun updatePassword(number: String, password: String): UpdatePasswordResponse

    suspend fun getStates(): StatesResponse

    suspend fun getDistricts(state_id: String): DistrictsResponse

    suspend fun getTalukas(state_id: String, district_id: String): TalukasResponse

    suspend fun getSchools(
        state_id: String,
        district_id: String,
        taluka_id: String
    ): AllSchoolResponse

    suspend fun getDriver(school_id: String): DriverResponse

    suspend fun getParent(school_id: String): ParentsResponse

    suspend fun getParentProfile(parentId: String): ParentsResponse

    suspend fun getDriverProfile(parentId: String): DriverResponse

    suspend fun registerSchool(schoolRegistrationRequest: SchoolRegistrationRequest): SchoolRegistrationResponse

    suspend fun getAllSchools(): AllSchoolResponse

    suspend fun getDriverRequests(driver_id: String): DriverRequestResponse

    suspend fun registerDriver(
        ownerName: String,
        contactNumber: String,
        password: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        address: String,
        aadharPhoto: String,
        licensePhoto: String,
        vehicleRegNumber: String,
        vehicleModel: String,
        seatingCapacity: String,
        vehicleType: String,
        insurancePhoto: String,
        fitnessCertificate: String,
        vehiclePhoto: String,
        areasCovered: String,
        schoolServiced: String,
        profilePicture: String,
        aboutMe: String,
        verificationState: String,
        availabilityStatus: String,
        termsAccepted: String,
        referalCode: String,
        referby: String,
        registeration_date: String
    ): RegistrationResponse

    suspend fun registerParent(
        parentName: String,
        contactNumber: String,
        password: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        address: String,
        childName: String,
        childClass: String,
        schoolName: String,
        dob: String,
        pickupLocation: String,
        dropOffLocation: String,
        numberOfChildren: String,
        emergencyContact: String,
        termsAccepted: String,
        refralCode: String,
        refralby: String,
        registeration_date:String
    ): RegistrationResponse

    suspend fun assignedVehicle(school_id: String, id: String): AssignedResponse

    suspend fun assignedStudent(school_id: String, id: String): AssignedResponse

    suspend fun getDriverByMob(mobile_no: String): DriverByMobResponse

    suspend fun sendAssignRequest(
        vehicle_id: String,
        parent_id: String
    ): SendRequestToDriverResponse

    suspend fun updateAssignRequest(vehicle_id: String, status: String): AssignRequestAcpRejResponse

    suspend fun updateProfileParent(
        id: String,
        parentName: String,
        contactNumber: String,
        parentAddress: String,
        childName: String,
        childSchoolName: String,
        profile_picture: String?
    ): ProfileUpdateResponse

    suspend fun updateProfileDriver(
        id: String,
        driver_name: String,
        number: String,
        vehicle_number: String?,
        state: String,
        district: String,
        taluka: String?,
        city: String?,
        address: String?,
        school_serviced: String?,
        profile_picture: String?
    ): ProfileUpdateResponse

    suspend fun updateVehiclePhotos(request: UpdateVehicleImageRequest): ProfileUpdateResponse

    suspend fun updatePaymentStatus(
        id: Int,
        paymentId: String,
        amount: String,
        paymentStatus: String,
        expireDate: String,
        paymentDate: String,
        assignStatus: String,
        assignDate: String,
        signature: String,
        orderId: String
    ): UpdatePaymentResponse

    suspend fun getVehiclePhotos(driver_id: String): VehiclePhotosResponse

    suspend fun driverSchoolOnRegister(
        vehiclesId: String,
        schoolId: String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String
    ): RegisterSchoolResponse

    suspend fun parentSchoolOnRegister(
        parentId: String,
        schoolId: String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String
    ): RegisterSchoolResponse

    suspend fun uploadDocumentsToDatabase(
        id: String,
        profile_picture: String,
        adhar_number: String,
        driver_license: String,
        insurance_detail: String,
        fitness_certificat: String,
        photo_of_vehicle: String
    ): DocumentUploadResponse

    suspend fun checkReferBy(referby: String): ReferByResponse

    suspend fun getCommission(driver_id: String): CommissionResponse

    suspend fun getCommissionParent(parent_id: String): CommissionParentResponse

    suspend fun getAllParents(): ParentsResponse

    suspend fun sendMessageToDriverFromParent(request: SendMessageRequest): MessageResponse

    suspend fun sendMessageToParentFromDriver(request: SendMessageRequest): MessageResponse

    suspend fun getParentMessage(parent_id: String): GetParentMessagesResponse

    suspend fun getDriverMessage(driver_id: String): GetDriverMessagesResponse

    suspend fun getDriverAllMessage(driver_id: String): GetDriverAllMessagesResponse

    suspend fun getOrderId(): RazorpayOrderCreationResponse

    suspend fun updateFCMToken(id: Int?, role: String, token: String): UpdateTokenResponse

    suspend fun getClasses(): GetClassesResponse

    suspend fun withdrawCommission(request: WithdrawRequest): WithdrawResponse

    suspend fun withdrawCommissionStatus(userId: String,role: String): WithdrawRequestStatus

    suspend fun couponValidation(parentId:String, couponCode:String): CouponValidationResponse

}
