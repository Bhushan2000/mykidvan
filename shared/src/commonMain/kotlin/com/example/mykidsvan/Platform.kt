package com.example.mykidsvan

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform