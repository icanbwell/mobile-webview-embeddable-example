import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.bwell_embeddable_android"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.bwell_embeddable_android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Environment variables from local.properties
        buildConfigField("String", "BWELL_ENVIRONMENT", "\"${localProperties.getProperty("BWELL_ENVIRONMENT", "")}\"")
        buildConfigField("String", "BWELL_CLIENT_ID", "\"${localProperties.getProperty("BWELL_CLIENT_ID", "")}\"")
        buildConfigField("String", "CLIENT_USER_TOKEN", "\"${localProperties.getProperty("CLIENT_USER_TOKEN", "")}\"")
        buildConfigField("String", "INITIAL_PATH", "\"${localProperties.getProperty("INITIAL_PATH", "")}\"")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}