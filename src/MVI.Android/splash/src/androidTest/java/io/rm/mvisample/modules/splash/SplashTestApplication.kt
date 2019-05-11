package io.rm.mvisample.modules.splash

import android.app.Activity
import androidx.multidex.MultiDexApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class SplashTestApplication : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    lateinit var applicationComponent: SplashTestApplicationComponent

    override fun onCreate() {
        super.onCreate()

        this.applicationComponent =
            DaggerSplashTestApplicationComponent.builder().application(this).build()
        this.applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return this.activityDispatchingAndroidInjector
    }
}