package io.rm.mvisample.core.data

import android.content.res.Resources
import io.rm.mvi.service.Result
import io.rm.mvisample.core.R
import io.rm.mvisample.core.SafeJobExecutor
import io.rm.mvisample.core.data.entity.Forecast
import io.rm.mvisample.core.data.local.AppDatabase
import io.rm.mvisample.core.data.remote.ApiOpenWeatherMap
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast
import io.rm.mvisample.core.data.remote.await
import kotlinx.coroutines.CoroutineDispatcher

interface ForecastRepositoryInput {
    suspend fun getQuickForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast>
    suspend fun updateForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast>
}

class ForecastRepository(
    private val local: ForecastLocalRepositoryInput,
    private val remote: ForecastRemoteRepositoryInput,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : ForecastRepositoryInput {
    override suspend fun getQuickForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast> =
        this.safeJobExecutor.safeCall(
            call = {
                this.local.getForecast(request)
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_forecast_repository_get_forecast)
        )

    override suspend fun updateForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast> =
        this.safeJobExecutor.safeCall(
            call = {
                when (val remoteResult = this.remote.getForecast(request)) {
                    is Result.Success -> {
                        when (val localResult = local.setForecast(request.cityId, remoteResult.data.forecasts)) {
                            is Result.Success -> {
                                remoteResult
                            }
                            is Result.Error -> {
                                localResult
                            }
                        }
                    }
                    is Result.Error -> {
                        remoteResult
                    }
                }
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_forecast_repository_get_forecast)
        )
}

interface ForecastLocalRepositoryInput {
    suspend fun getForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast>
    suspend fun setForecast(cityId: String, forecasts: List<Forecast>): Result<Unit>
}

class ForecastLocalRepository(
    private val appDatabase: AppDatabase,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : ForecastLocalRepositoryInput {
    override suspend fun getForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast> =
        this.safeJobExecutor.safeCall(
            call = {
                Result.Success(
                    data = ResponseGetCityForecast(
                        forecasts = this.appDatabase.forecastDao().getForecastByCityId(
                            request.cityId
                        )
                    )
                )
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_forecast_repository_get_forecast)
        )

    override suspend fun setForecast(cityId: String, forecasts: List<Forecast>) = this.safeJobExecutor.safeCall(
        call = {
            this.appDatabase.forecastDao().deleteForecastByCityId(cityId)
            Result.Success(data = this.appDatabase.forecastDao().setForecast(forecasts))
        },
        context = this.jobDispatcher,
        errorMessage = this.resources.getString(R.string.core_error_forecast_repository_get_forecast)
    )
}

interface ForecastRemoteRepositoryInput {
    suspend fun getForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast>
}

class ForecastRemoteRepository(
    private val openWeatherMap: ApiOpenWeatherMap,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : ForecastRemoteRepositoryInput {
    override suspend fun getForecast(request: RequestGetCityForecast): Result<ResponseGetCityForecast> =
        this.safeJobExecutor.safeCall(
            call = {
                Result.Success(data = this.openWeatherMap.getCityForecast(request.cityId, request.count).await())
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_forecast_repository_get_forecast)
        )
}