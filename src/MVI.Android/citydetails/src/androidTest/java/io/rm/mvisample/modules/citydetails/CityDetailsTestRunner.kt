package io.rm.mvisample.modules.citydetails

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CityDetailsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, CityDetailsTestApplication::class.java.name, context)
    }
}