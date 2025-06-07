package com.example.mykidsvan.android.data

import com.example.mykidsvan.android.data.dto.request.DriverMessageToAllParentsRequest
import com.example.mykidsvan.android.data.dto.request.DriverMessageToIndividualParentsRequest
import com.example.mykidsvan.android.data.dto.request.ParentMessageToDriverRequest
import com.example.mykidsvan.android.data.dto.request.ReferByResponse
import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.example.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.example.mykidsvan.android.data.dto.response.AllSchoolResponse
import com.example.mykidsvan.android.data.dto.response.AssignedResponse
import com.example.mykidsvan.android.data.dto.response.CommissionResponse
import com.example.mykidsvan.android.data.dto.response.DistrictsResponse
import com.example.mykidsvan.android.data.dto.response.DocumentUploadResponse
import com.example.mykidsvan.android.data.dto.response.DriverByMobResponse
import com.example.mykidsvan.android.data.dto.response.DriverMessageIndividualPMsgResponse
import com.example.mykidsvan.android.data.dto.response.DriverMessageAllPMsgResponse
import com.example.mykidsvan.android.data.dto.response.DriverRequestResponse
import com.example.mykidsvan.android.data.dto.response.DriverResponse
import com.example.mykidsvan.android.data.dto.response.GetDriverAllMessagesResponse
import com.example.mykidsvan.android.data.dto.response.GetDriverMessagesResponse
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.GetParentMessagesResponse
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

// Domain Layer: AuthRepository.kt
interface AuthRepository {

    suspend fun login(email: String, password: String): LoginResponse

    suspend fun sendOtp(number: String): OtpResponse

    suspend fun resendOtp(number: String): OtpResponse

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
        referby: String
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
        refralby: String
    ): RegistrationResponse

    suspend fun assignedVehicle(school_id: String, id: String): AssignedResponse

    suspend fun assignedStudent(school_id: String, id: String): AssignedResponse

    suspend fun getDriverByMob(mobile_no: String): DriverByMobResponse

    // map
    suspend fun sendLatLong(lat: String, long: String, id: String): SendLatLongResponse

    suspend fun getLatLong(driver_id: String): GetLatLongResponse

    suspend fun sendAssignRequest(
        vehicle_id: String,
        parent_id: String
    ): SendRequestToDriverResponse

    suspend fun updateAssignRequest(vehicle_id: String, status: String): SendLatLongResponse

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
        transactionId: String,
        amount: String,
        paymentStatus: String,
        expireDate: String,
        paymentDate: String,
        assignStatus: String,
        assignDate: String
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

    suspend fun getAllParents(): ParentsResponse

    suspend fun sendMessageToDriverFromParent(request: ParentMessageToDriverRequest): ParentMessageResponse

    suspend fun sendMessageToAllParentFromDriver(request: DriverMessageToAllParentsRequest): DriverMessageAllPMsgResponse

    suspend fun sendMessageToIndividualParentFromDriver(request: DriverMessageToIndividualParentsRequest): DriverMessageIndividualPMsgResponse

    suspend fun getParentMessage(parent_id: String): GetParentMessagesResponse

    suspend fun getDriverMessage(driver_id: String): GetDriverMessagesResponse

    suspend fun getDriverAllMessage(driver_id: String): GetDriverAllMessagesResponse

}
