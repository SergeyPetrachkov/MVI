package io.rm.mvisample.modules.splash

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, SplashAppModule::class, SplashActivitiesModule::class])
interface SplashTestApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(value: Application): Builder

        fun build(): SplashTestApplicationComponent
    }

    fun inject(application: SplashTestApplication)
    fun inject(splashInstrumentalTest: SplashInstrumentalTest)
}