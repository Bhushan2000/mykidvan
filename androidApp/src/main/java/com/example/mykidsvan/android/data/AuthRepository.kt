package com.example.mykidsvan.android.data

import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.example.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.example.mykidsvan.android.data.dto.response.AllSchoolResponse
import com.example.mykidsvan.android.data.dto.response.AssignedResponse
import com.example.mykidsvan.android.data.dto.response.DistrictsResponse
import com.example.mykidsvan.android.data.dto.response.DriverByMobResponse
import com.example.mykidsvan.android.data.dto.response.DriverRequestResponse
import com.example.mykidsvan.android.data.dto.response.DriverResponse
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.LoginResponse
import com.example.mykidsvan.android.data.dto.response.OtpResponse
import com.example.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.example.mykidsvan.android.data.dto.response.ParentsResponse
import com.example.mykidsvan.android.data.dto.response.ProfileUpdateResponse
import com.example.mykidsvan.android.data.dto.response.RegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SchoolRegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse

import com.example.mykidsvan.android.data.dto.response.StatesResponse
import com.example.mykidsvan.android.data.dto.response.TalukasResponse
import com.example.mykidsvan.android.data.dto.response.UpdatePasswordResponse
import com.google.gson.annotations.SerializedName

// Domain Layer: AuthRepository.kt
interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse

    suspend fun sendOtp(
        number: String
    ): OtpResponse

    suspend fun resendOtp(
        number: String
    ): OtpResponse

    suspend fun verifyOtp(
        number: String,
        otp: String
    ): OtpVerificationResponse

    suspend fun updatePassword(
        number: String,
        password: String
    ): UpdatePasswordResponse

    suspend fun getStates(
    ): StatesResponse

    suspend fun getDistricts(
        state_id: String
    ): DistrictsResponse

    suspend fun getTalukas(
        state_id: String,
        district_id: String
    ): TalukasResponse

    suspend fun getSchools(
        state_id: String,
        district_id: String,
        taluka_id: String
    ): AllSchoolResponse

    suspend fun getDriver(
        school_id: String
    ): DriverResponse

    suspend fun getParent(
        school_id: String
    ): ParentsResponse

    suspend fun getParentProfile(
        parentId: String
    ): ParentsResponse

    suspend fun getDriverProfile(
        parentId: String
    ): DriverResponse

    suspend fun registerSchool(
        schoolRegistrationRequest: SchoolRegistrationRequest
    ): SchoolRegistrationResponse

    suspend fun getAllSchools(): AllSchoolResponse

    suspend fun getDriverRequests(driver_id: String, parent_id: String): DriverRequestResponse

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
        termsAccepted: String
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
        termsAccepted: String
    ): RegistrationResponse

    suspend fun assignedVehicle(
        school_id: String,
        id: String
    ): AssignedResponse

    suspend fun assignedStudent(
        school_id: String,
        id: String
    ): AssignedResponse

    suspend fun getDriverByMob(mobile_no: String): DriverByMobResponse

    // map
    suspend fun sendLatLong(
        lat: String,
        long: String,
         id: String
    ): SendLatLongResponse

    suspend fun getLatLong(driver_id: String): GetLatLongResponse

    suspend fun sendAssignRequest(
        vehicle_id: String,
        parent_id: String
    ): SendRequestToDriverResponse

    suspend fun updateAssignRequest(
        vehicle_id: String,
        status: String
    ): SendLatLongResponse

    suspend fun updateProfileParent(
        id: String,
        parentName: String,
        contactNumber: String,
        password: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        parentAddress: String,
        childName: String,
        childSchoolName: String,
        childClass: String,
        childDob: String,
        pickUp: String,
        dropOff: String,
        numberOfChlid: String,
        emergencyContact: String,
        termsCondition: String,
        role: String,
        schoolId: String
    ): ProfileUpdateResponse

    suspend fun updateProfileDriver(
        id: String,
        driver_name: String,
        driver_type: String?,
        number: String,
        vehicle_number: String?,
        state: String,
        district: String,
        taluka: String?,
        city: String?,
        address: String?,
        adhar_number: String?,
        driver_license: String?,
        vehicle_registration: String?,
        vehicle_model: String?,
        seating_capacity: String?,
        insurance_details: String?,
        fintness_certificate: String?,
        photo_of_vehicle: String?,
        areas_covered: String?,
        school_serviced: String?,
        profile_picture: String?,
        about_me: String?,
        veritication_status: String?,
        availability_status: String?,
        terms_and_condition: String?,
        latitude: String?,
        longitude: String?,
        timer: String?,
        role: String?,
        username: String?,
        password: String,
        status: String,
        school_id: String
    ): ProfileUpdateResponse

    suspend fun updateVehiclePhotos(request: UpdateVehicleImageRequest): ProfileUpdateResponse
}
