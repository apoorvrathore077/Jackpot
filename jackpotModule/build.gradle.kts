plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.playzelo.jackpotmodule"
    compileSdk = 36

    viewBinding {
        enable = true
    }
    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.daimajia.androidanimations:library:2.4@aar")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.cheonjaeung.powerwheelpicker.android:powerwheelpicker:1.0.0")



    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.1.0")

    implementation("com.google.android.material:material:1.12.0")
    implementation("com.airbnb.android:lottie:6.4.0")

    implementation("androidx.interpolator:interpolator:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Retrofit Core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // JSON Converter (Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (optional but recommended for logging)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")


    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}