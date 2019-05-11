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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(project(":mvi"))
    api(Config.Dependency.KOTLIN)
    api(Config.Dependency.APPCOMPAT)
    api(Config.Dependency.CONSTRAINT_LAYOUT)
    api(Config.Dependency.RECYCLER_VIEW)
    api(Config.Dependency.KOTLIN_COROUTINES_CORE)
    api(Config.Dependency.KOTLIN_COROUTINES_ANDROID)
    api(Config.Dependency.DAGGER)
    kapt(Config.Dependency.DAGGER_COMPILER)
    api(Config.Dependency.DAGGER_ANDROID_SUPPORT)
    kapt(Config.Dependency.DAGGER_ANDROID_PROCESSOR)
    api(Config.Dependency.RETROFIT)
    api(Config.Dependency.RETROFIT_CONVERTER_GSON)
    api(Config.Dependency.ROOM)
    kapt(Config.Dependency.ROOM_PROCESSOR)
    implementation(Config.Dependency.MULTIDEX)
    implementation(Config.Dependency.ROOM_KTX)
    implementation(Config.Dependency.ROOM_TEST_HELPERS)
    testImplementation(Config.Dependency.TEST_CORE)
    testImplementation(Config.Dependency.ROBOLECTRIC)
    androidTestImplementation(Config.Dependency.TEST_CORE)
    androidTestImplementation(Config.Dependency.ESPRESSO_CORE)
    androidTestImplementation(Config.Dependency.MOCKITO_KOTLIN)
    androidTestImplementation(Config.Dependency.MOCKITO_ANDROID)
    androidTestImplementation(Config.Dependency.RETROFIT_MOCK)
}
