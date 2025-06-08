package com.vihaanshika.mykidsvan.android.utils

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val message: String) : PaymentState()
    data class Error(val error: String) : PaymentState()
}
