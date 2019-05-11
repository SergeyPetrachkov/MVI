package io.rm.mvisample.core.data

import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import io.rm.mvi.service.Result
import io.rm.mvisample.core.R
import io.rm.mvisample.core.SafeJobExecutor
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.local.AppDatabase
import io.rm.mvisample.core.data.remote.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

interface CitiesRepositoryInput {
    suspend fun getCities(): Result<ResponseGetCities>
    suspend fun addCityByName(request: RequestAddCityByName): Result<ResponseAddCityByName>
    suspend fun removeCityById(request: RequestRemoveCityById): Result<ResponseRemoveCityById>
    suspend fun updateCities(): Result<ResponseUpdateCities>
}

class CitiesRepository(
    private val local: CitiesRepositoryLocalInput,
    private val remote: CitiesRepositoryRemoteInput,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : CitiesRepositoryInput {
    override suspend fun getCities(): Result<ResponseGetCities> =
        this.safeJobExecutor.safeCall(
            call = {
                when (val cachedCities = this.local.getCities()) {
                    is Result.Success -> {
                        if (cachedCities.data.list.isEmpty()) {
                            initDefaultCities()
                        } else {
                            cachedCities
                        }
                    }
                    is Result.Error -> {
                        cachedCities
                    }
                }
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_cities_repository_get_cities)
        )

    override suspend fun addCityByName(request: RequestAddCityByName): Result<ResponseAddCityByName> =
        this.safeJobExecutor.safeCall(
            call = {
                when (val remoteResult = this.remote.getCityByName(request)) {
                    is Result.Success -> {
                        this.local.addCity(remoteResult.data.city)
                    }
                    is Result.Error -> remoteResult
                }
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_cities_repository_get_city_by_name)
        )

    override suspend fun removeCityById(request: RequestRemoveCityById): Result<ResponseRemoveCityById> =
        this.safeJobExecutor.safeCall(
            call = {
                this.local.removeCity(request.cityId)
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_unknown)
        )

    override suspend fun updateCities(): Result<ResponseUpdateCities> = this.safeJobExecutor.safeCall(
        call = {
            when (val localResult = this.local.getCities()) {
                is Result.Success -> {
                    if (localResult.data.list.isEmpty()) {
                        when (val initResult = initDefaultCities()) {
                            is Result.Success -> {
                                Result.Success(data = ResponseUpdateCities(list = initResult.data.list))
                            }
                            is Result.Error -> {
                                initResult
                            }
                        }
                    } else {
                        val citiesIds =
                            localResult.data.list.map { it.id }.joinToString(separator = ",")
                        when (val remoteResult =
                            this.remote.getCities(request = RequestGetCities(citiesIds))) {
                            is Result.Success -> {
                                this.local.setCities(remoteResult.data.list)
                                Result.Success(data = ResponseUpdateCities(list = remoteResult.data.list))
                            }
                            is Result.Error -> {
                                remoteResult
                            }
                        }
                    }
                }
                is Result.Error -> localResult
            }
        },
        context = this.jobDispatcher,
        errorMessage = this.resources.getString(R.string.core_error_cities_repository_get_cities)
    )

    private suspend fun initDefaultCities(): Result<ResponseGetCities> {
        val citiesIds =
            this.resources.getIntArray(R.array.default_cities)
                .joinToString(separator = ",")

        return when (val remoteCities =
            this.remote.getCities(request = RequestGetCities(citiesIds = citiesIds))) {
            is Result.Success -> {
                when (val result = this.local.setCities(remoteCities.data.list)) {
                    is Result.Success -> {
                        remoteCities
                    }
                    is Result.Error -> {
                        result
                    }
                }
            }
            is Result.Error -> {
                remoteCities
            }
        }
    }
}

interface CitiesRepositoryLocalInput {
    suspend fun getCities(): Result<ResponseGetCities>
    suspend fun setCities(cities: List<City>): Result<Unit>
    suspend fun addCity(city: City): Result<ResponseAddCityByName>
    suspend fun removeCity(cityId: Int): Result<ResponseRemoveCityById>
}

class CitiesRepositoryLocal(
    private val appDatabase: AppDatabase,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : CitiesRepositoryLocalInput {
    override suspend fun getCities(): Result<ResponseGetCities> = this.safeJobExecutor.safeCall(
        call = {
            Result.Success(data = ResponseGetCities(this.appDatabase.cityDao().getCities()))
        },
        context = this.jobDispatcher,
        errorMessage = this.resources.getString(R.string.core_error_cities_repository_local_get_cities)
    )

    override suspend fun setCities(cities: List<City>) = this.safeJobExecutor.safeCall(
        call = {
            this.appDatabase.cityDao().deleteAll()
            Result.Success(data = this.appDatabase.cityDao().setCities(cities))
        },
        context = this.jobDispatcher,
        errorMessage = this.resources.getString(R.string.core_error_cities_repository_local_set_cities)
    )

    override suspend fun addCity(city: City): Result<ResponseAddCityByName> =
        safeJobExecutor.safeCall(
            call = {
                try {
                    this.appDatabase.cityDao().addCity(city)
                    Result.Success(data = ResponseAddCityByName(city))
                } catch (e: SQLiteConstraintException) {
                    throw IllegalStateException(
                        this.resources.getString(R.string.core_error_cities_repository_add_city_by_name)
                    )
                }
            },
            context = this.jobDispatcher,
            errorMessage = null
        )

    override suspend fun removeCity(cityId: Int): Result<ResponseRemoveCityById> =
        safeJobExecutor.safeCall(
            call = {
                this.appDatabase.cityDao().deleteCityById(cityId)
                Result.Success(data = ResponseRemoveCityById(cityId))
            },
            context = this.jobDispatcher,
            errorMessage = this.resources.getString(R.string.core_error_unknown)
        )
}

interface CitiesRepositoryRemoteInput {
    suspend fun getCities(request: RequestGetCities): Result<ResponseGetCities>
    suspend fun getCityByName(request: RequestAddCityByName): Result<ResponseAddCityByName>
}

class CitiesRepositoryRemote(
    private val openWeatherMap: ApiOpenWeatherMap,
    private val safeJobExecutor: SafeJobExecutor,
    private val jobDispatcher: CoroutineDispatcher,
    private val resources: Resources
) : CitiesRepositoryRemoteInput {
    override suspend fun getCities(request: RequestGetCities) = this.safeJobExecutor.safeCall(
        call = {
            Result.Success(ResponseGetCities(this.openWeatherMap.getCities(request.citiesIds).await().list))
        },
        context = this.jobDispatcher,
        errorMessage = this.resources.getString(R.string.core_error_cities_repository_get_cities)
    )

    override suspend fun getCityByName(request: RequestAddCityByName): Result<ResponseAddCityByName> =
        withContext(this.jobDispatcher) {
            try {
                Result.Success(
                    ResponseAddCityByName(
                        this@CitiesRepositoryRemote.openWeatherMap.getCityByName(request.cityName)
                            .await()
                    )
                )
            } catch (e: Throwable) {
                val errorMessage = with(this@CitiesRepositoryRemote.resources) {
                    when (e) {
                        is HttpException -> {
                            if (e.response().code() == HttpURLConnection.HTTP_NOT_FOUND) {
                                getString(R.string.core_error_cities_repository_get_city_by_name)
                            } else {
                                getString(R.string.core_error_network)
                            }
                        }
                        else -> {
                            getString(R.string.core_error_network)
                        }
                    }
                }

                Result.Error(IOException(errorMessage))
            }
        }
}