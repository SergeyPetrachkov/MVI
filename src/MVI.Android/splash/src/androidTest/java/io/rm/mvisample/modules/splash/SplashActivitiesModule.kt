package io.rm.mvisample.modules.splash

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.rm.mvisample.modules.citieslist.CitiesListActivity
import io.rm.mvisample.modules.citieslist.CitiesListModule

@Module
abstract class SplashActivitiesModule {
    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [CitiesListModule::class])
    abstract fun bindCitiesListActivity(): CitiesListActivity
}