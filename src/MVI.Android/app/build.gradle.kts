import io.rm.android.build.Config

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Config.Sdk.COMPILE_SDK_VERSION)
    defaultConfig {
        applicationId = "io.rm.mvisample"
        minSdkVersion(Config.Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Config.Sdk.TARGET_SDK_VERSION)
        multiDexEnabled = true
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
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(project(":mvi"))
    implementation(project(":core"))
    implementation(project(":splash"))
    implementation(project(":citieslist"))
    implementation(project(":citydetails"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Config.Dependency.MULTIDEX)
    kapt(Config.Dependency.DAGGER_COMPILER)
    kapt(Config.Dependency.DAGGER_ANDROID_PROCESSOR)
    testImplementation(project(":mvi"))
    testImplementation(Config.Dependency.JUNIT)
    androidTestImplementation(project(":mvi"))
    androidTestImplementation(Config.Dependency.TEST_CORE)
    androidTestImplementation(Config.Dependency.TEST_EXTENSIONS)
    androidTestImplementation(Config.Dependency.TEST_RULES)
    androidTestImplementation(Config.Dependency.TEST_RUNNER)

    androidTestImplementation(Config.Dependency.ESPRESSO_CORE)
}