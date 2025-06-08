package com.vihaanshika.mykidsvan.android.data.dto.request

data class SchoolRegistrationRequest(
    val school_name: String,
    val contact_number: String,
    val state: String,
    val district: String,
    val taluka: String,
    val city: String,
    val school_address: String
)
