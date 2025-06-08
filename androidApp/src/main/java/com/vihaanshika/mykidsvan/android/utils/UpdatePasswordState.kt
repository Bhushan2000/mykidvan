package com.vihaanshika.mykidsvan.android.utils

data class UpdatePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordUpdated: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
