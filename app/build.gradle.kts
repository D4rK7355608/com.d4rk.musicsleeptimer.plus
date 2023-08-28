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
        versionCode = 25
        versionName = "2.6_r1"
        archivesName = "${applicationId}-v${versionName}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("en", "de", "es", "fr", "hi", "hu", "in", "it", "ja", "ro", "ru", "tr", "sv", "bg", "pl", "uk")
    }
    buildTypes {
        getByName("release") {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.4.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    implementation("com.google.firebase:firebase-perf:20.4.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.multidex:multidex:2.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}