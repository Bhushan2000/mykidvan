package com.example.mykidsvan.android.utils

data class OtpState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val message: String? = null
)