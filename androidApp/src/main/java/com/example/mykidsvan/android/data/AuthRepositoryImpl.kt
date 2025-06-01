package com.example.mykidsvan.android.data

import com.example.mykidsvan.android.data.dto.request.AssignRequest
import com.example.mykidsvan.android.data.dto.request.DriverUpdateRequest
import com.example.mykidsvan.android.data.dto.request.OtpRequest
import com.example.mykidsvan.android.data.dto.request.OtpVerificationRequest
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


class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return api.login(mapOf("number" to username, "password" to password))
    }

    override suspend fun registerDriver(
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
    ): RegistrationResponse {
        val requestBodyMap = mapOf(
            "driver_name" to ownerName,
            "password" to password,
            "number" to contactNumber,
            "password" to password,
            "vehicle_number" to vehicleRegNumber,
            "state" to state,
            "district" to district,
            "taluka" to taluka,
            "city" to city,
            "address" to address,
            "adhar_number" to aadharPhoto,
            "driver_license" to licensePhoto,
            "vehicle_registration" to vehicleRegNumber,
            "vehicle_model" to vehicleModel,
            "seating_capacity" to seatingCapacity,
            "driver_type" to vehicleType,
            "insurance_details" to insurancePhoto,
            "fintness_certificate" to fitnessCertificate,
            "photo_of_vehicle" to vehiclePhoto,
            "areas_covered" to areasCovered,
            "school_serviced" to schoolServiced,
            "profile_picture" to profilePicture,
            "about_me" to aboutMe,
            "veritication_status" to verificationState,
            "availability_status" to availabilityStatus,
            "terms_and_condition" to termsAccepted,
            "refer_id" to referalCode,
            "refer_by" to referby
        )
        return api.registerDriver(requestBodyMap)
    }


    override suspend fun registerParent(
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
        referalCode:String,
        referby: String
    ): RegistrationResponse {
        val registerParentRequest = ParentRegistrationRequest(
            parentName,
            contactNumber,
            password,
            state,
            district,
            taluka,
            city,
            address,
            childName,
            schoolName,
            childClass,
            dob,
            pickupLocation,
            dropOffLocation,
            numberOfChildren,
            emergencyContact,
            termsAccepted,
            referalCode,
            referby
        )
        return api.registerParent(registerParentRequest)
    }

    override suspend fun assignedVehicle(school_id: String, id: String): AssignedResponse {
        val request = AssignRequest(school_id, id)
        return api.assignedVehicle(request)
    }

    override suspend fun assignedStudent(school_id: String, id: String): AssignedResponse {
        val request = AssignRequest(school_id, id)
        return api.assignedStudent(request)
    }

    override suspend fun getDriverByMob(mobile_no: String): DriverByMobResponse {
        return api.getDriverByMobNo(mobile_no)
    }

    override suspend fun sendOtp(mobile_number: String): OtpResponse {
        val otpRequest = OtpRequest(mobile_number)  // Create an OtpRequest object
        return api.sendOtp(otpRequest) // Pass the OtpRequest object
    }

    override suspend fun resendOtp(mobile_number: String): OtpResponse {
        val otpRequest = OtpRequest(mobile_number)  // Create an OtpRequest object
        return api.sendOtp(otpRequest) // Pass the OtpRequest object
    }

    override suspend fun verifyOtp(mobile_number: String, otp: String): OtpVerificationResponse {
        val otpVerifyRequest =
            OtpVerificationRequest(mobile_number, otp)  // Create an OtpVerifyRequest object
        return api.verifyOtp(otpVerifyRequest)
    }

    override suspend fun updatePassword(number: String, password: String): UpdatePasswordResponse {
        val updatePasswordRequest = UpdatePasswordRequest(number, password)
        return api.updatePassword(updatePasswordRequest)
    }

    override suspend fun getStates(): StatesResponse {
        return api.getState()
    }

    override suspend fun getDistricts(state_id: String): DistrictsResponse {
        return api.getDistrict(state_id)
    }

    override suspend fun getTalukas(state_id: String, district_id: String): TalukasResponse {
        return api.getTaluka(state_id, district_id)
    }

    override suspend fun getSchools(
        state_id: String,
        district_id: String,
        taluka_id: String
    ): AllSchoolResponse {
        return api.getSchools(state_id, district_id, taluka_id)
    }

    override suspend fun getDriver(school_id: String): DriverResponse {
        return api.getDriver(school_id)
    }

    override suspend fun getParent(school_id: String): ParentsResponse {
        return api.getParents(school_id)
    }

    override suspend fun getParentProfile(parentId: String): ParentsResponse {
        return api.getParentProfile(parentId)
    }

    override suspend fun getDriverProfile(driverId: String): DriverResponse {
        return api.getDriverProfile(driverId)
    }

    override suspend fun registerSchool(schoolRegistrationRequest: SchoolRegistrationRequest): SchoolRegistrationResponse {
        return api.registerSchools(schoolRegistrationRequest)
    }

    override suspend fun getAllSchools(): AllSchoolResponse {
        return api.getAllSchools()
    }

    override suspend fun getDriverRequests(
        driver_id: String
    ): DriverRequestResponse {
        return api.getDriverRequests(driver_id)
    }

    override suspend fun sendLatLong(
        lat: String,
        long: String,
        id: String
    ): SendLatLongResponse {
        val request = SendLatLongRequest(id, lat, long)
        return api.sendLatLong(request)
    }

    override suspend fun getLatLong(driver_id: String): GetLatLongResponse {
        return api.getLatLong(driver_id)
    }

    override suspend fun sendAssignRequest(
        vehicle_id: String,
        parent_id: String
    ): SendRequestToDriverResponse {
        val request = SendAssignRequest(vehicle_id, parent_id)
        return api.sendAssignRequest(request)
    }

    override suspend fun updateAssignRequest(
        vehicle_id: String,
        status: String
    ): SendLatLongResponse {
        val request = UpdateAssignRequest(vehicle_id, status)
        return api.updateAssignRequest(request)
    }

    override suspend fun updateProfileParent(
        id: String,
        parentName: String,
        contactNumber: String,
        parentAddress: String,
        childName: String,
        childSchoolName: String,
        profile_picture: String?
    ): ProfileUpdateResponse {
        val request = ParentUpdateRequest(
            id = id,
            parentName = parentName,
            contactNumber = contactNumber,
            parentAddress = parentAddress,
            childName = childName,
            childSchoolName = childSchoolName,
            profilePicture = profile_picture
        )
        return api.updateProfileParent(request)
    }


    override suspend fun updateProfileDriver(
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
    ): ProfileUpdateResponse {
        val request = DriverUpdateRequest(
            id = id,
            driverName = driver_name,
            number = number,
            vehicleNumber = vehicle_number,
            state = state,
            district = district,
            taluka = taluka,
            city = city,
            address = address,
            schoolServiced = school_serviced,
            profilePicture = profile_picture
        )
        return api.updateProfileDriver(request)
    }

    override suspend fun updateVehiclePhotos(request: UpdateVehicleImageRequest): ProfileUpdateResponse {
        return api.updateVehiclePhotos(request)
    }

    override suspend fun updatePaymentStatus(
        id: Int,
        transactionId: String,
        amount: String,
        paymentStatus: String,
        expireDate: String,
        paymentDate: String,
        assignStatus: String,
        assignDate: String
    ): UpdatePaymentResponse {
        val request = PaymentUpdateRequest(
            id,
            transactionId,
            amount,
            paymentStatus,
            expireDate,
            paymentDate,
            assignStatus,
            assignDate
        )
        return api.updatePayment(request)
    }

    override suspend fun getVehiclePhotos(driver_id: String): VehiclePhotosResponse {
        return api.getVehiclePhotos(driver_id)
    }

    override suspend fun driverSchoolOnRegister(
        vehiclesId: String,
        schoolId:String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String
    ): RegisterSchoolResponse {
        val request = RegisterSchoolDriverRequest(vehiclesId,schoolId,schoolName,contactNumber,state,district,taluka,city,schoolAddress)
        return api.driverSchoolOnRegister(request)
    }

    override suspend fun parentSchoolOnRegister(
        parentId: String,
        schoolId:String,
        schoolName: String,
        contactNumber: String,
        state: String,
        district: String,
        taluka: String,
        city: String,
        schoolAddress: String
    ): RegisterSchoolResponse {
        val request = RegisterSchoolParentRequest(parentId,schoolId,schoolName,contactNumber,state,district,taluka,city,schoolAddress)
        return api.parentSchoolOnRegister(request)    }

    override suspend fun uploadDocumentsToDatabase(
        id: String,
        profile_picture: String,
        adhar_number: String,
        driver_license: String,
        insurance_detail: String,
        fitness_certificat: String,
        photo_of_vehicle: String
    ): DocumentUploadResponse{
        val request = mapOf(
            "id" to id,
            "profile_picture" to profile_picture,
            "adhar_number" to adhar_number,
            "driver_license" to driver_license,
            "insurance_details" to insurance_detail,
            "fitness_certificate" to fitness_certificat,
            "photo_of_vehicle" to photo_of_vehicle
        )
        return api.uploadDocumentsToDatabase(request)
    }

    override suspend fun checkReferBy(referby: String): ReferByResponse {
        val request = ReferByRequest(referby)
        return api.checkReferCode(request)
    }

    override suspend fun getCommission(driver_id: String): CommissionResponse {
        return api.getCommission(driver_id)
    }

    override suspend fun getAllParents(): ParentsResponse {
        return api.getAlParents()
    }

}
