package io.rm.mvisample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.rm.mvisample.modules.citieslist.CitiesListActivity
import io.rm.mvisample.modules.citieslist.CitiesListModule
import io.rm.mvisample.modules.citydetails.CityDetailsActivity
import io.rm.mvisample.modules.citydetails.CityDetailsModule
import io.rm.mvisample.modules.splash.SplashActivity
import io.rm.mvisample.modules.splash.SplashModule

@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [CitiesListModule::class])
    abstract fun bindCitiesListActivity(): CitiesListActivity

    @ContributesAndroidInjector(modules = [CityDetailsModule::class])
    abstract fun bindCityDetailsActivity(): CityDetailsActivity
}