plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.land_parcel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.land_parcel"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            buildFeatures {
                viewBinding = true
                dataBinding = true
            }
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

  //Mapbox Dependency
    implementation ("com.mapbox.mapboxsdk:mapbox-android-sdk:9.2.0"){
        exclude(group = "group_name", module = "module_name")    }

    //Image Show Library
    implementation (libs.glide)

    //Network Libraray
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.retrofit2.converter.simplexml)

    //Convert base32 from coordinate
    implementation (libs.jts.core)
    implementation (libs.jackson.databind)
    implementation (libs.jackson.module.kotlin)

    // Use this GeoJSON dependency (for older Mapbox SDK)
    implementation(libs.mapbox.sdk.geojson)
    implementation (libs.gson)


    // Room Database
    implementation(libs.androidx.room.ktx)
    implementation (libs.androidx.room.runtime)
    ksp (libs.androidx.room.compiler)

    // AWS S3 SDK
    implementation (libs.aws.android.sdk.s3)
    implementation (libs.aws.android.sdk.mobile.client)

    // ViewModel and LiveData
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    //for decode jwt
    implementation(libs.jwt)

    // WorkManager for background syncing
    implementation (libs.androidx.work.runtime.ktx)

    // FileProvider for secure file access
    implementation (libs.androidx.core.ktx.v1101)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    //MVVM
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

    //viewpager
    implementation (libs.androidx.viewpager2)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // PNIL Generate
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation ("org.locationtech.jts:jts-core:1.18.2")
    implementation ("org.wololo:jts2geojson:0.18.1")
    implementation ("org.locationtech.jts:jts-core:1.18.2")


}