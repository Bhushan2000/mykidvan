package com.vihaanshika.mykidsvan.android.data.dto.response

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
    @SerializedName("school_id") var schoolId: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("payment_status") var paymentStatus: String? = null,
    @SerializedName("expire_date") var expireDate: String? = null,
    @SerializedName("payment_date") var paymentDate: String? = null,
    @SerializedName("assign_status") var assignStatus: String? = null,
    @SerializedName("assign_date") var assignDate: String? = null,
    @SerializedName("vehicle_id") var vehicleId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("refer_id") var referId: String? = null,
    @SerializedName("refer_by") var referBy: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null,
    @SerializedName("school_state") var schoolState: String? = null,
    @SerializedName("school_district") var schoolDistrict: String? = null,
    @SerializedName("school_taluka") var schoolTaluka: String? = null
)
