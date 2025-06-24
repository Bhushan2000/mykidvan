import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}
val secrets = Properties().apply {
    load(rootProject.file("secrets.properties").inputStream())
}
android {
    namespace = "com.vihaanshika.mykidsvan.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.vihaanshika.mykidsvan.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.3"
        //buildConfigField("String", "API_KEY", "\"${API_KEY}\"")
        //buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY")}\"")

        // new way using secrets.properties.
        buildConfigField("String", "MAPS_API_KEY", "\"${secrets["MAPS_API_KEY"]}\"")
        buildConfigField("String", "RAZORPAY_SECRET", "\"${secrets["RAZORPAY_SECRET"]}\"")
        buildConfigField("String", "RAZORPAY_ID", "\"${secrets["RAZORPAY_ID"]}\"")

        // Optional: Use for manifest placeholder
        manifestPlaceholders["MAPS_API_KEY"] = secrets["MAPS_API_KEY"] ?: ""
        manifestPlaceholders["RAZORPAY_ID"] = secrets["RAZORPAY_ID"] ?: ""


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
    debugImplementation(libs.compose.ui.tooling)
    // Navigation Compose
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
    // Accompanist Navigation Animation dependency :
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")
    // lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    // razorpay
    implementation("com.razorpay:checkout:1.6.40")
    // local broadcastmanager
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    // service class
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    // Pager (optional)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    // Compose Material 3
    implementation("androidx.compose.material3:material3:1.2.0")
    // BOM: Bill of Materials to manage versions of related libraries
    implementation(platform("com.google.auth:google-auth-library-bom:1.30.1"))
    // OAuth2 + HTTP support (for GoogleCredentials)
    implementation("com.google.auth:google-auth-library-oauth2-http")
    // Import the BoM for the Firebase platform
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    // FCM
    implementation("com.google.firebase:firebase-messaging:24.1.1")
    // crashlytics
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.crashlytics)
    // in-app update
    implementation("com.google.android.play:app-update:2.1.0")
    // For Kotlin users also import the Kotlin extensions library for Play In-App Update:
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}