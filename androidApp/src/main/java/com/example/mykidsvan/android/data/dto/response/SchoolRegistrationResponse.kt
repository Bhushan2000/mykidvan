package com.example.mykidsvan.android.data.dto.response

data class SchoolRegistrationResponse(
    val status: Boolean,
    val message: String,
    val id: Int,
    val districts: List<String>,
    val talukas: List<String>
)
