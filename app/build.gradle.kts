plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.khan.fftracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.khan.fftracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled= true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    buildFeatures {
        viewBinding= true
    }
    buildFeatures {
        dataBinding= true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")


    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // ViewModel
    val lifecycle_version= "2.7.0"
    implementation ("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata:$lifecycle_version")
    // Lifecycles only (without ViewModel or LiveData)
    implementation ("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")

    // Saved state module for ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    implementation ("androidx.lifecycle:lifecycle-process:$lifecycle_version")


    //Caroutines
    val couritines_version = "1.7.1"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$couritines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$couritines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$couritines_version")


    //retrofit and gson
    implementation ("com.google.code.gson:gson:2.8.7")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.dagger:dagger:2.51")
    kapt("com.google.dagger:dagger-compiler:2.51")


    implementation ("com.google.firebase:firebase-core:17.5.0")
    implementation ("com.google.firebase:firebase-messaging:20.2.4")
    implementation ("com.crashlytics.sdk.android:crashlytics:2.10.1")
    implementation ("com.google.android.gms:play-services-location:17.0.0")
    implementation ("com.github.rey5137:material:1.2.5")
    implementation ("com.journeyapps:zxing-android-embedded:3.5.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("com.google.android.gms:play-services-vision:20.1.1")
    //       use the Google Wallet API
    implementation ("com.google.android.gms:play-services-pay:16.0.3")
    implementation ("androidx.multidex:multidex:2.0.1")

    implementation ("com.github.bumptech.glide:glide:4.13.0") // Replace with your Glide version
    kapt ("com.github.bumptech.glide:compiler:4.13.0")  // Replace with your Glide version
    implementation ("com.squareup.picasso:picasso:2.5.2")

    //textView Number Counter
    implementation ("com.github.hamzaahmedkhan:CounterAnimationTextView:1.0.1")

}
// Add to the bottom of the file
apply(plugin = "com.google.gms.google-services")