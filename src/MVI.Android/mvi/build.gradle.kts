import io.rm.android.build.Config

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Config.Sdk.COMPILE_SDK_VERSION)
    defaultConfig {
        minSdkVersion(Config.Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Config.Sdk.TARGET_SDK_VERSION)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Config.Dependency.KOTLIN)
    implementation(Config.Dependency.APPCOMPAT)
    implementation(Config.Dependency.CONSTRAINT_LAYOUT)
    implementation(Config.Dependency.RECYCLER_VIEW)
    implementation(Config.Dependency.KOTLIN_COROUTINES_CORE)
    implementation(Config.Dependency.KOTLIN_COROUTINES_ANDROID)
    implementation(Config.Dependency.DAGGER)
    kapt(Config.Dependency.DAGGER_COMPILER)
    implementation(Config.Dependency.DAGGER_ANDROID_SUPPORT)
    kapt(Config.Dependency.DAGGER_ANDROID_PROCESSOR)
    testImplementation(Config.Dependency.JUNIT)
    androidTestImplementation(Config.Dependency.TEST_CORE)
    androidTestImplementation(Config.Dependency.TEST_EXTENSIONS)
    androidTestImplementation(Config.Dependency.TEST_RULES)
    androidTestImplementation(Config.Dependency.TEST_RUNNER)
    androidTestImplementation(Config.Dependency.ESPRESSO_CORE)
}