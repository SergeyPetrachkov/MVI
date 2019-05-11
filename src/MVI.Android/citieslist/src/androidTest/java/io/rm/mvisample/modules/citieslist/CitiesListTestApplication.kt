package io.rm.mvisample.modules.citieslist

import android.app.Activity
import androidx.multidex.MultiDexApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class CitiesListTestApplication : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    lateinit var applicationComponent: CitiesListTestApplicationComponent

    override fun onCreate() {
        super.onCreate()

        this.applicationComponent =
            DaggerCitiesListTestApplicationComponent.builder().application(this).build()
        this.applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return this.activityDispatchingAndroidInjector
    }
}