package io.rm.mvisample.modules.citydetails

import android.app.Activity
import androidx.multidex.MultiDexApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class CityDetailsTestApplication : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    lateinit var applicationComponent: CityDetailsTestApplicationComponent

    override fun onCreate() {
        super.onCreate()

        this.applicationComponent =
            DaggerCityDetailsTestApplicationComponent.builder().application(this).build()
        this.applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return this.activityDispatchingAndroidInjector
    }
}