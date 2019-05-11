package io.rm.mvisample.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import io.rm.mvisample.main.ViperSampleApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivitiesModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(value: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: ViperSampleApplication)
}