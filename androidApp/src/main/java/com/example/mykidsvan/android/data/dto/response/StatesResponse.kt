package com.example.mykidsvan.android.data.dto.response

data class StatesResponse(
    val status: Boolean,
    val message: String,
    val data: List<State>
)

data class State(
    val id: String,
    val state_name: String
)




