package com.example.mykidsvan.android.data.dto.response

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val id: String,
    val role: String?,          // Nullable because "role" may be null in the response
    val password: String,
    val driver_name: String     // Fixed the space in "driver_name "
)
