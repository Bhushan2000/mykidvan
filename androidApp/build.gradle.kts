plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.mykidsvan.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.mykidsvan.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.crashlytics.buildtools)
    debugImplementation(libs.compose.ui.tooling)
    implementation("androidx.navigation:navigation-compose:2.8.9")
    // Retrofit for REST API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")
    // Jetpack Datastore for login state persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // maps
    implementation("com.google.maps.android:maps-compose:2.11.2")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.accompanist:accompanist-permissions:0.33.0-alpha")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    //coil
    implementation("io.coil-kt:coil-compose:2.4.0") // Use the latest version if available
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Accompanist Navigation Animation dependency :
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")
    // lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    // razorpay
    implementation("com.razorpay:checkout:1.6.33")
    // Jetpack Datastore for login state persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // coil
    implementation("io.coil-kt:coil-compose:2.4.0") // for AsyncImage
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

}