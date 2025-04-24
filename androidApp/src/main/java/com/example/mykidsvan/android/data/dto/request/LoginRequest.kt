package com.example.mykidsvan.android.data.dto.request

// Example request and response data classes
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val message: String?
)
