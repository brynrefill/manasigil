plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // id("com.android.application")
    // add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.brynrefill.manasigil"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.brynrefill.manasigil"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime) // added by Android Studio
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // import the BoM for the Firebase platform.
    // When using the BoM, don't specify versions in Firebase library dependencies
    // because the app will always use compatible versions of the Firebase Android libraries
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    // dependencies for Firebase products to use in Manasigil
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.compose.material:material-icons-extended") // added for password toggle visibility icons

    // add dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")

    // add encryption library for secure password storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // add dependency for biometric authentication
    implementation("androidx.biometric:biometric:1.1.0")

    // add Retrofit dependencies to integrate external API services.
    // Simplify API calls and handles parsing JSON into Java/Kotlin objects
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}
