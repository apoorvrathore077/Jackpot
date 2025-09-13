import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    id("maven-publish")
}


android {
    namespace = "com.playzelo.jackpot"
    compileSdk = 36
    viewBinding{
        enable = true
    }

    defaultConfig {
        applicationId = "com.playzelo.jackpot"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(project(":jackpotModule"))
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}