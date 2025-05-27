package com.example.mykidsvan.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class ParentUpdateRequest(
    @SerializedName("id") var id: String? = null,
    @SerializedName("parent_name") var parentName: String? = null,
    @SerializedName("contact_number") var contactNumber: String? = null,
    @SerializedName("parent_address") var parentAddress: String? = null,
    @SerializedName("child_name") var childName: String? = null,
    @SerializedName("child_school_name") var childSchoolName: String? = null,
    @SerializedName("profile_picture") var profilePicture: String? = null
)