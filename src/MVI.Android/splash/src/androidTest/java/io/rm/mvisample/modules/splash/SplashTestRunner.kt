package io.rm.mvisample.modules.splash

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class SplashTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, SplashTestApplication::class.java.name, context)
    }
}