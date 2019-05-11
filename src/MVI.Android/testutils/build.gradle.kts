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
        testInstrumentationRunner = "io.rm.mvisample.modules.citieslist.CitiesListTestRunner"
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Config.Dependency.RECYCLER_VIEW)
    implementation(Config.Dependency.MATERIAL)
    implementation(Config.Dependency.MULTIDEX)
    implementation(Config.Dependency.TEST_CORE)
    implementation(Config.Dependency.TEST_EXTENSIONS)
    implementation(Config.Dependency.ESPRESSO_CORE)
    implementation(Config.Dependency.ESPRESSO_INTENTS)
    implementation(Config.Dependency.ESPRESSO_CONTRIB)
    implementation(Config.Dependency.MOCKITO_KOTLIN)
    implementation(Config.Dependency.MOCKITO_ANDROID)
}