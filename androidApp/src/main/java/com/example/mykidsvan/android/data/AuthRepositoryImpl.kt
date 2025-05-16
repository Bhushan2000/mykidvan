package com.example.mykidsvan.android.data

import com.example.mykidsvan.android.data.dto.request.AssignRequest
import com.example.mykidsvan.android.data.dto.request.OtpRequest
import com.example.mykidsvan.android.data.dto.request.OtpVerificationRequest
import com.example.mykidsvan.android.data.dto.request.ParentRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.SchoolRegistrationRequest
import com.example.mykidsvan.android.data.dto.request.SendAssignRequest
import com.example.mykidsvan.android.data.dto.request.SendLatLongRequest
import com.example.mykidsvan.android.data.dto.request.SendRequestToDriverResponse
import com.example.mykidsvan.android.data.dto.request.UpdateAssignRequest
import com.example.mykidsvan.android.data.dto.request.UpdatePasswordRequest
import com.example.mykidsvan.android.data.dto.request.UpdateVehicleImageRequest
import com.example.mykidsvan.android.data.dto.response.AllSchoolResponse
import com.example.mykidsvan.android.data.dto.response.AssignedResponse
import com.example.mykidsvan.android.data.dto.response.DistrictsResponse
import com.example.mykidsvan.android.data.dto.response.Driver
import com.example.mykidsvan.android.data.dto.response.DriverByMobResponse
import com.example.mykidsvan.android.data.dto.response.DriverRequestResponse
import com.example.mykidsvan.android.data.dto.response.DriverResponse
import com.example.mykidsvan.android.data.dto.response.GetLatLongResponse
import com.example.mykidsvan.android.data.dto.response.LoginResponse
import com.example.mykidsvan.android.data.dto.response.OtpResponse
import com.example.mykidsvan.android.data.dto.response.OtpVerificationResponse
import com.example.mykidsvan.android.data.dto.response.Parent
import com.example.mykidsvan.android.data.dto.response.ParentsResponse
import com.example.mykidsvan.android.data.dto.response.ProfileUpdateResponse
import com.example.mykidsvan.android.data.dto.response.RegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SchoolRegistrationResponse
import com.example.mykidsvan.android.data.dto.response.SendLatLongResponse
import com.example.mykidsvan.android.data.dto.response.StatesResponse
import com.example.mykidsvan.android.data.dto.response.TalukasResponse
import com.example.mykidsvan.android.data.dto.response.UpdatePasswordResponse
import com.example.mykidsvan.android.data.dto.response.UpdatePaymentResponse


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
        termsAccepted: String
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
        termsAccepted: String
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
            termsAccepted
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
    ): ProfileUpdateResponse {
        val request = Parent(
            id = id,
            parentName = parentName,
            contactNumber = contactNumber,
            password = password,
            state = state,
            district = district,
            taluka = taluka,
            city = city,
            parentAddress = parentAddress,
            childName = childName,
            childSchoolName = childSchoolName,
            childClass = childClass,
            childDob = childDob,
            pickUp = pickUp,
            dropOff = dropOff,
            numberOfChlid = numberOfChlid,
            emergencyContact = emergencyContact,
            termsCondition = termsCondition,
            role = role,
            schoolId = schoolId
        )
        return api.updateProfileParent(request)
    }


    override suspend fun updateProfileDriver(
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
    ): ProfileUpdateResponse {
        val request = Driver(
            id = id,
            driver_name = driver_name,
            driver_type = driver_type,
            number = number,
            vehicle_number = vehicle_number,
            state = state,
            district = district,
            taluka = taluka,
            city = city,
            address = address,
            adhar_number = adhar_number,
            driver_license = driver_license,
            vehicle_registration = vehicle_registration,
            vehicle_model = vehicle_model,
            seating_capacity = seating_capacity,
            insurance_details = insurance_details,
            fintness_certificate = fintness_certificate,
            photo_of_vehicle = photo_of_vehicle,
            areas_covered = areas_covered,
            school_serviced = school_serviced,
            profile_picture = profile_picture,
            about_me = about_me,
            veritication_status = veritication_status,
            availability_status = availability_status,
            terms_and_condition = terms_and_condition,
            latitude = latitude,
            longitude = longitude,
            timer = timer,
            role = role,
            username = username,
            password = password,
            status = status,
            school_id = school_id
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
        val requestBody = mapOf(
            "id" to id,
            "transaction_id" to transactionId,
            "amount" to amount,
            "payment_status" to paymentStatus,
            "expire_date" to expireDate,
            "payment_date" to paymentDate,
            "assign_status" to assignStatus,
            "assign_date" to assignDate
        )
        return api.updatePayment(requestBody)
    }
}
