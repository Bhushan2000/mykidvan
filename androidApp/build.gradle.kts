plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.vihaanshika.mykidsvan.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.vihaanshika.mykidsvan.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
        //buildConfigField("String", "API_KEY", "\"${API_KEY}\"")
        buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY")}\"")

    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {

            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/INDEX.LIST",
                "mozilla/public-suffix-list.txt"
            )
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
    implementation(libs.firebase.crashlytics)
    debugImplementation(libs.compose.ui.tooling)
    implementation("androidx.navigation:navigation-compose:2.8.9")
    // Retrofit for REST API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")
    // Jetpack Datastore for login state persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // maps
    implementation("com.google.maps.android:maps-compose:2.11.4")
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
    implementation("com.razorpay:checkout:1.6.40")
    // Jetpack Datastore for login state persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // coil
    implementation("io.coil-kt:coil-compose:2.4.0") // for AsyncImage
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    // Pager (optional)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    // Compose Material 3
    implementation("androidx.compose.material3:material3:1.2.0")
    // FCM
    implementation("com.google.firebase:firebase-messaging:24.1.1")

    // BOM: Bill of Materials to manage versions of related libraries
    implementation(platform("com.google.auth:google-auth-library-bom:1.30.1"))

    // OAuth2 + HTTP support (for GoogleCredentials)
    implementation("com.google.auth:google-auth-library-oauth2-http")
    // Websocket
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-websockets:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.ktor:ktor-client-cio:2.3.7") // 👈 This is needed for CIO
}