package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class DriverRequestResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("Message") var Message: String? = null,
    @SerializedName("data") var data: ArrayList<RequestData> = arrayListOf()
)

data class RequestData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("vehicle_id") var vehicleId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("parent_name") var parentName: String? = null,
    @SerializedName("contact_number") var contactNumber: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("taluka") var taluka: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("parent_address") var parentAddress: String? = null,
    @SerializedName("child_name") var childName: String? = null,
    @SerializedName("child_school_name") var childSchoolName: String? = null,
    @SerializedName("child_class") var childClass: String? = null,
    @SerializedName("child_dob") var childDob: String? = null,
    @SerializedName("pick_up") var pickUp: String? = null,
    @SerializedName("drop_off") var dropOff: String? = null,
    @SerializedName("number_of_chlid") var numberOfChlid: String? = null,
    @SerializedName("emergency_contact") var emergencyContact: String? = null,
    @SerializedName("terms_condition") var termsCondition: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("school_id") var schoolId: String? = null
)