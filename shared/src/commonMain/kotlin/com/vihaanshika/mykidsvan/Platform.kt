package com.vihaanshika.mykidsvan

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform