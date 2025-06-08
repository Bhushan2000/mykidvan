package com.vihaanshika.mykidsvan.android.utils

data class UnifiedMessage(
    val message: String?,
    val createdAt: String,
    val name: String?,
    val profileUrl: String?,
    val senderType: String // "parent" or "driver"
)