package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class ParentsResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Parent> = arrayListOf()
)

data class Parent(
    @SerializedName("id") var id: String? = null,
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
