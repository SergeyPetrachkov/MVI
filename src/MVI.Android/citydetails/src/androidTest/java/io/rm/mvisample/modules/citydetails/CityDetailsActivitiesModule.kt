package io.rm.mvisample.modules.citydetails

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CityDetailsActivitiesModule {
    @ContributesAndroidInjector(modules = [CityDetailsModule::class])
    abstract fun bindCityDetailsActivity(): CityDetailsActivity
}