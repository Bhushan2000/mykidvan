package com.vihaanshika.mykidsvan.android.data.dto.request

data class ParentRegistrationRequest(
    val parent_name: String,
    val contact_number: String,
    val password: String,
    val state: String,
    val district: String,
    val taluka: String,
    val city: String,
    val parent_address: String,
    val child_name: String,
    val child_school_name: String,
    val child_class: String,
    val child_dob: String,             // Assuming format "YYYY-MM-DD"
    val pick_up: String,
    val drop_off: String,
    val number_of_chlid: String,       // You can change it to Int if needed
    val emergency_contact: String,
    val terms_condition: String,        // Assuming "true"/"false" as String
    val refer_id: String,
    val refer_by: String,
    val registeration_date: String
)
