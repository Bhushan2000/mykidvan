package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName


data class DriverResponse(
    val status: Boolean,
    val message: String,
    val data: List<DriverMob>
)


data class Driver(
    val id: String,
    val driver_name: String,
    val driver_type: String?,
    val number: String,
    val vehicle_number: String?,
    val state: String,
    val district: String,
    val taluka: String?,
    val city: String?,
    val address: String?,
    val adhar_number: String?,
    val driver_license: String?,
    val vehicle_registration: String?,
    val vehicle_model: String?,
    val seating_capacity: String?,
    val insurance_details: String?,
    val fintness_certificate: String?,
    val photo_of_vehicle: String?,
    val areas_covered: String?,
    val school_serviced: String?,
    val profile_picture: String?,
    val about_me: String?,
    val veritication_status: String?,
    val availability_status: String?,
    val terms_and_condition: String?,
    val latitude: String?,
    val longitude: String?,
    val timer: String?,
    val role: String?,
    val username: String?,
    val password: String,
    val status: String,
    val school_id: String,
    var refer_id: String? = null,
    var refer_by: String? = null,
    var amount: String? = null,
    var school_state: String? = null,
    var school_district: String? = null,
    var school_taluka: String? = null
)




