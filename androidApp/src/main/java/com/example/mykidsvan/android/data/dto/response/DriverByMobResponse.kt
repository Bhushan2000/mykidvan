package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class DriverByMobResponse(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("vehicle") var driverMob: DriverMob? = DriverMob(),
    @SerializedName("sent_message") var sentMessage: SentMessage? = SentMessage()

)

data class DriverMob(

    @SerializedName("id") var id: String? = null,
    @SerializedName("driver_name") var driverName: String? = null,
    @SerializedName("driver_type") var driverType: String? = null,
    @SerializedName("number") var number: String? = null,
    @SerializedName("vehicle_number") var vehicleNumber: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("taluka") var taluka: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("adhar_number") var adharNumber: String? = null,
    @SerializedName("driver_license") var driverLicense: String? = null,
    @SerializedName("vehicle_registration") var vehicleRegistration: String? = null,
    @SerializedName("vehicle_model") var vehicleModel: String? = null,
    @SerializedName("seating_capacity") var seatingCapacity: String? = null,
    @SerializedName("insurance_details") var insuranceDetails: String? = null,
    @SerializedName("fintness_certificate") var fintnessCertificate: String? = null,
    @SerializedName("photo_of_vehicle") var photoOfVehicle: String? = null,
    @SerializedName("areas_covered") var areasCovered: String? = null,
    @SerializedName("school_serviced") var schoolServiced: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null,
    @SerializedName("about_me") var aboutMe: String? = null,
    @SerializedName("veritication_status") var veriticationStatus: String? = null,
    @SerializedName("availability_status") var availabilityStatus: String? = null,
    @SerializedName("terms_and_condition") var termsAndCondition: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("timer") var timer: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("school_id") var schoolId: String? = null

)

data class SentMessage(

    @SerializedName("vehicle_id") var vehicleId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null

)