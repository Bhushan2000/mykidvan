package com.example.mykidsvan.android.utils

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val exception: Throwable? = null) : ResultState<Nothing>()
}
