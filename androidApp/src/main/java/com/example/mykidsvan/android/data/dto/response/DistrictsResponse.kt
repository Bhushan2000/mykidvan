package com.example.mykidsvan.android.data.dto.response

data class DistrictsResponse(
    val status: Boolean,
    val message: String,
    val data: List<District>
)

data class District(
    val id: String,
    val state_id: String,
    val district_name: String
)