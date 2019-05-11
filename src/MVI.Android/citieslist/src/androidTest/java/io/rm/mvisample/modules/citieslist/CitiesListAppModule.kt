package io.rm.mvisample.modules.citieslist

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.CoroutineService
import io.rm.mvi.service.InteractorCoroutineService
import io.rm.mvi.service.Result
import io.rm.mvisample.core.AppDefaultCoroutineExceptionHandler
import io.rm.mvisample.core.DefaultCoroutineExceptionHandler
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.core.data.ForecastRepositoryInput
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseUpdateCities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
class CitiesListAppModule {

    @Singleton
    @Provides
    fun citiesRepository(): CitiesRepositoryInput {
        return mock {
            onBlocking {
                updateCities()
            } doReturn Result.Success(ResponseUpdateCities(listOf()))
        }
    }

    @Singleton
    @Provides
    fun forecastRepository(): ForecastRepositoryInput {
        return mock {
            onBlocking {
                this.getQuickForecast(RequestGetCityForecast("123", 5))
            } doReturn Result.Success(ResponseGetCityForecast(listOf()))
        }
    }

    @Provides
    fun defaultCoroutineExceptionHandler(): DefaultCoroutineExceptionHandler {
        return AppDefaultCoroutineExceptionHandler()
    }

    @Provides
    fun coroutineService(@Named(DependencyNames.UI_DISPATCHER) uiDispatcher: CoroutineDispatcher): CoroutineService {
        return InteractorCoroutineService(uiDispatcher)
    }

    @Singleton
    @Provides
    @Named(DependencyNames.UI_DISPATCHER)
    fun uiCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}