import org.gradle.api.JavaVersion.VERSION_21

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ruiniles.glucoview"
    compileSdk = 36

    kotlin {
        compilerOptions {
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
            freeCompilerArgs.add("-opt-in=com.google.android.horologist.annotations.ExperimentalHorologistApi")
        }
    }

    defaultConfig {
        applicationId = "com.ruiniles.glucoview"
        minSdk = 32
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(platform(libs.androidx.compose.bom))


    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.tiles)
    implementation(libs.androidx.tiles.material)
    implementation(libs.androidx.tiles.tooling.preview)
    implementation(libs.androidx.watchface.complications.data.source.ktx)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.android)
    implementation(libs.androidx.datastore.preferences.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.protolayout.material)
    implementation(libs.androidx.protolayout.material3)

    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)

    implementation(libs.material)
    implementation(libs.play.services.wearable)
    implementation(libs.wear.compose.material3)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.tiles.tooling)
}