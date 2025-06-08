package com.vihaanshika.mykidsvan.android.data.dto.response

data class TalukasResponse(
    val status: Boolean,
    val message: String,
    val data: List<Taluka>
)

data class Taluka(
    val id: String,
    val state_id: String,
    val district_id: String,
    val taluka_name: String
)