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
    implementation(project(":core"))
    implementation(project(":citydetails"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Config.Dependency.RECYCLER_VIEW)
    implementation(Config.Dependency.MATERIAL)
    implementation(Config.Dependency.MULTIDEX)
    kapt(Config.Dependency.DAGGER_COMPILER)
    kapt(Config.Dependency.DAGGER_ANDROID_PROCESSOR)
    testImplementation(Config.Dependency.JUNIT)
    androidTestImplementation(project(":testutils"))
    androidTestImplementation(Config.Dependency.TEST_CORE)
    androidTestImplementation(Config.Dependency.TEST_EXTENSIONS)
    androidTestImplementation(Config.Dependency.ESPRESSO_CORE)
    androidTestImplementation(Config.Dependency.ESPRESSO_INTENTS)
    androidTestImplementation(Config.Dependency.ESPRESSO_CONTRIB)
    androidTestImplementation(Config.Dependency.MOCKITO_KOTLIN)
    androidTestImplementation(Config.Dependency.MOCKITO_ANDROID)
    androidTestImplementation(Config.Dependency.DAGGER)
    androidTestImplementation(Config.Dependency.DAGGER_ANDROID_SUPPORT)
    kaptAndroidTest(Config.Dependency.DAGGER_COMPILER)
    kaptAndroidTest(Config.Dependency.DAGGER_ANDROID_PROCESSOR)
}