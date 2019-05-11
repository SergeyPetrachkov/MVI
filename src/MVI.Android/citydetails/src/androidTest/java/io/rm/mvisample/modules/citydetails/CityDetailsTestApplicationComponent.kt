package io.rm.mvisample.modules.citydetails

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, CityDetailsAppModule::class, CityDetailsActivitiesModule::class])
interface CityDetailsTestApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(value: Application): Builder

        fun build(): CityDetailsTestApplicationComponent
    }

    fun inject(application: CityDetailsTestApplication)
    fun inject(cityDetailsInstrumentalTest: CityDetailsInstrumentalTest)
}