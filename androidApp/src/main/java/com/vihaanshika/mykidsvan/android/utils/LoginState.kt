package com.vihaanshika.mykidsvan.android.utils

import com.vihaanshika.mykidsvan.android.data.dto.response.DriverData
import com.vihaanshika.mykidsvan.android.data.dto.response.ParentData

data class LoginState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val driver: DriverData? = null,
    val parent: ParentData? = null,
    val error: String? = null
)