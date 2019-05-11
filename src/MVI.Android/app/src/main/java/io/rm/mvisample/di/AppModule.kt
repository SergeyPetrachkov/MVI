package io.rm.mvisample.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.CoroutineService
import io.rm.mvi.service.InteractorCoroutineService
import io.rm.mvisample.core.AppDefaultCoroutineExceptionHandler
import io.rm.mvisample.core.AppSafeJobExecutor
import io.rm.mvisample.core.DefaultCoroutineExceptionHandler
import io.rm.mvisample.core.SafeJobExecutor
import io.rm.mvisample.core.data.CitiesRepository
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.core.data.CitiesRepositoryLocal
import io.rm.mvisample.core.data.CitiesRepositoryLocalInput
import io.rm.mvisample.core.data.CitiesRepositoryRemote
import io.rm.mvisample.core.data.CitiesRepositoryRemoteInput
import io.rm.mvisample.core.data.ForecastLocalRepository
import io.rm.mvisample.core.data.ForecastLocalRepositoryInput
import io.rm.mvisample.core.data.ForecastRemoteRepository
import io.rm.mvisample.core.data.ForecastRemoteRepositoryInput
import io.rm.mvisample.core.data.ForecastRepository
import io.rm.mvisample.core.data.ForecastRepositoryInput
import io.rm.mvisample.core.data.local.AppDatabase
import io.rm.mvisample.core.data.remote.ApiOpenWeatherMap
import io.rm.mvisample.core.data.remote.OpenWeatherMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    @Named(DependencyNames.IO_DISPATCHER)
    fun ioCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @Provides
    @Named(DependencyNames.DB_DISPATCHER)
    fun dbCoroutineDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

    @Singleton
    @Provides
    @Named(DependencyNames.UI_DISPATCHER)
    fun uiCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Singleton
    @Provides
    fun applicationContext(application: Application): Context {
        return application
    }

    @Singleton
    @Provides
    fun resources(applicationContext: Context): Resources {
        return applicationContext.resources
    }

    @Provides
    fun safeJobExecutor(applicationContext: Context): SafeJobExecutor {
        return AppSafeJobExecutor(applicationContext)
    }

    @Provides
    fun coroutineService(@Named(DependencyNames.UI_DISPATCHER) uiDispatcher: CoroutineDispatcher): CoroutineService {
        return InteractorCoroutineService(uiDispatcher)
    }

    @Singleton
    @Provides
    fun apiOpenWeatherMap(resources: Resources): ApiOpenWeatherMap {
        return OpenWeatherMap(resources).api
    }

    @Provides
    fun defaultCoroutineExceptionHandler(): DefaultCoroutineExceptionHandler {
        return AppDefaultCoroutineExceptionHandler()
    }

    @Singleton
    @Provides
    fun citiesRepository(
        safeJobExecutor: SafeJobExecutor,
        @Named(DependencyNames.IO_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        citiesRepositoryRemoteInput: CitiesRepositoryRemoteInput,
        citiesRepositoryLocalInput: CitiesRepositoryLocalInput,
        resources: Resources
    ): CitiesRepositoryInput {
        return CitiesRepository(
            local = citiesRepositoryLocalInput,
            remote = citiesRepositoryRemoteInput,
            safeJobExecutor = safeJobExecutor,
            jobDispatcher = jobDispatcher,
            resources = resources
        )
    }

    @Singleton
    @Provides
    fun citiesRepositoryRemote(
        safeJobExecutor: SafeJobExecutor,
        apiOpenWeatherMap: ApiOpenWeatherMap,
        @Named(DependencyNames.IO_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        resources: Resources
    ): CitiesRepositoryRemoteInput {
        return CitiesRepositoryRemote(apiOpenWeatherMap, safeJobExecutor, jobDispatcher, resources)
    }

    @Singleton
    @Provides
    fun citiesRepositoryLocal(
        safeJobExecutor: SafeJobExecutor,
        appDatabase: AppDatabase,
        @Named(DependencyNames.DB_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        resources: Resources
    ): CitiesRepositoryLocalInput {
        return CitiesRepositoryLocal(appDatabase, safeJobExecutor, jobDispatcher, resources)
    }

    @Singleton
    @Provides
    fun forecastRepository(
        local: ForecastLocalRepositoryInput,
        remote: ForecastRemoteRepositoryInput,
        safeJobExecutor: SafeJobExecutor,
        @Named(DependencyNames.IO_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        resources: Resources
    ): ForecastRepositoryInput {
        return ForecastRepository(local, remote, safeJobExecutor, jobDispatcher, resources)
    }

    @Singleton
    @Provides
    fun forecastLocalRepository(
        appDatabase: AppDatabase,
        safeJobExecutor: SafeJobExecutor,
        @Named(DependencyNames.DB_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        resources: Resources
    ): ForecastLocalRepositoryInput {
        return ForecastLocalRepository(appDatabase, safeJobExecutor, jobDispatcher, resources)
    }

    @Singleton
    @Provides
    fun forecastRemoteRepository(
        apiOpenWeatherMap: ApiOpenWeatherMap,
        safeJobExecutor: SafeJobExecutor,
        @Named(DependencyNames.IO_DISPATCHER) jobDispatcher: CoroutineDispatcher,
        resources: Resources
    ): ForecastRemoteRepositoryInput {
        return ForecastRemoteRepository(
            apiOpenWeatherMap,
            safeJobExecutor,
            jobDispatcher,
            resources
        )
    }

    @Singleton
    @Provides
    fun appDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "weather_db")
            .build()
    }
}