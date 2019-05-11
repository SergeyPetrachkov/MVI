package io.rm.mvisample.modules.citieslist

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.rm.mvisample.modules.citydetails.CityDetailsActivity
import io.rm.mvisample.modules.citydetails.CityDetailsModule

@Module
abstract class CitiesListActivitiesModule {
    @ContributesAndroidInjector(modules = [CitiesListModule::class])
    abstract fun bindCitiesListActivity(): CitiesListActivity

    @ContributesAndroidInjector(modules = [CityDetailsModule::class])
    abstract fun bindCityDetailsActivity(): CityDetailsActivity
}