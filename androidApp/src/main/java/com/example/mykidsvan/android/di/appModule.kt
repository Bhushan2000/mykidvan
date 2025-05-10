package com.example.mykidsvan.android.di

 import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.mykidsvan.android.data.AuthRepository
import com.example.mykidsvan.android.data.AuthApi
import com.example.mykidsvan.android.data.AuthRepositoryImpl

import com.example.mykidsvan.android.utils.UserPreferences
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<AuthApi> {
        Retrofit.Builder()
            .baseUrl("https://avschoolerp.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    // Provide UserPreferences (with proper context)
    single { UserPreferences(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    viewModel { AuthViewModel(get(), get()) }

}