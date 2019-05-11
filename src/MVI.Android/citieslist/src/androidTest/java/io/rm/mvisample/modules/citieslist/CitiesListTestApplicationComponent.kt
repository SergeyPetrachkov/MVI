package io.rm.mvisample.modules.citieslist

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, CitiesListAppModule::class, CitiesListActivitiesModule::class])
interface CitiesListTestApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(value: Application): Builder

        fun build(): CitiesListTestApplicationComponent
    }

    fun inject(application: CitiesListTestApplication)
    fun inject(citiesListInstrumentalTest: CitiesListInstrumentalTest)
}