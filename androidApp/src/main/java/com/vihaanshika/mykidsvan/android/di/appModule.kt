package com.vihaanshika.mykidsvan.android.di

import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.example.maptracking.LatLngRepositoryImpl
import com.example.maptracking.LatLngViewModel
import com.vihaanshika.mykidsvan.android.data.AuthRepository
import com.vihaanshika.mykidsvan.android.data.AuthApi
import com.vihaanshika.mykidsvan.android.data.AuthRepositoryImpl
import com.vihaanshika.mykidsvan.android.data.MessagesViewModel
import com.vihaanshika.mykidsvan.android.ui.tracking.LatLngRepository
import com.vihaanshika.mykidsvan.android.ui.tracking.LocationTrackingService
import com.vihaanshika.mykidsvan.android.utils.APIEndpoints
import com.vihaanshika.mykidsvan.android.utils.LocationFetcher
import com.vihaanshika.mykidsvan.android.utils.UserPreferences
import com.vihaanshika.mykidsvan.android.utils.WebSocketManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<AuthApi> {
        // Create logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // You can also use BASIC or HEADERS
        }

        // Build OkHttpClient with the logging interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // Build Retrofit instance
        Retrofit.Builder()
            .baseUrl(APIEndpoints.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    // Provide UserPreferences (with proper context)
    single { UserPreferences(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<LatLngRepository> { LatLngRepositoryImpl(get()) }
    single { HttpClient(CIO) { install(WebSockets) } }
    single { WebSocketManager(get()) }
    viewModel { LatLngViewModel(get(), get(), androidContext()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { MessagesViewModel(get()) }
}