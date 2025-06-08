package com.vihaanshika.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetDriverAllMessagesResponse(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<GroupMessagesDriver> = arrayListOf()
)

data class GroupMessagesDriver(
    @SerializedName("id") var id: String? = null,
    @SerializedName("search_specific") var searchSpecific: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("vehicles_id") var vehiclesId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)