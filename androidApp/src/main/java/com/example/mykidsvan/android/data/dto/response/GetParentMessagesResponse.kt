package com.example.mykidsvan.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetParentMessagesResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Message> = arrayListOf()
)

data class Message(
    @SerializedName("id") var id: String? = null,
    @SerializedName("parent_id") var parentId: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)