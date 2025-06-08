package com.vihaanshika.mykidsvan.android.utils

// utils/Resource.kt
sealed class Resource<T> {
    class Idle<T> : Resource<T>()                      // ← Added this
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val throwable: Throwable? = null) : Resource<T>()
}
