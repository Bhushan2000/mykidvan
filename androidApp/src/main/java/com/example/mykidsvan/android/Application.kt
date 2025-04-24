package com.example.mykidsvan.android

import android.app.Application
import com.example.mykidsvan.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(appModule)
        }
    }
}

//├── data
//│   ├── api
//│   │   └── AuthApi.kt         # Retrofit API Service
//│   ├── repository
//│   │   └── AuthRepository.kt  # Repository Implementation
//│   └── model
//│       └── AuthResponse.kt    # Data classes (LoginResponse, SignupRequest)
//├── domain
//│   ├── repository
//│   │   └── AuthRepository.kt  # Repository Interface
//│   └── usecase
//│       ├── LoginUseCase.kt    # Use case for login
//│       └── SignupUseCase.kt   # Use case for signup
//├── presentation
//│   ├── login                  # Jetpack Compose UI Screens
//│   │   └── LoginScreen.kt
//│   ├── signup
//│   │   └── SignupScreen.kt
//│   └── viewmodel
//│       └── AuthViewModel.kt   # Shared ViewModel
//└── utils
//└── SessionManager.kt      # Session Persistence (DataStore)
