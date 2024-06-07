import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    compileSdk = 34
    namespace = "com.d4rk.musicsleeptimer.plus"
    defaultConfig {
        applicationId = "com.d4rk.musicsleeptimer.plus"
        minSdk = 26
        targetSdk = 34
        versionCode = 27
        versionName = "3.0.0"
        archivesName = "${applicationId}-v${versionName}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf(
            "en" ,
            "de" ,
            "es" ,
            "fr" ,
            "hi" ,
            "hu" ,
            "in" ,
            "it" ,
            "ja" ,
            "ro" ,
            "ru" ,
            "tr" ,
            "sv" ,
            "bg" ,
            "pl" ,
            "uk"
        )
    }
    buildTypes {
        release {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt") , "proguard-rules.pro"
            )
        }
        debug {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt") , "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    bundle {
        storeArchive {
            enable = true
        }
    }
}
dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.perf)
    implementation(libs.appcompat)
    implementation(libs.work.runtime.ktx)
    implementation(libs.multidex)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}