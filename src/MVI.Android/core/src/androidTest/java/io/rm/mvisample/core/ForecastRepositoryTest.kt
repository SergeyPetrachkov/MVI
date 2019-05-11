package io.rm.mvisample.core

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.ForecastLocalRepository
import io.rm.mvisample.core.data.ForecastRemoteRepository
import io.rm.mvisample.core.data.ForecastRepository
import io.rm.mvisample.core.data.ForecastRepositoryInput
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.entity.Forecast
import io.rm.mvisample.core.data.local.AppDatabase
import io.rm.mvisample.core.data.local.CityDao
import io.rm.mvisample.core.data.local.ForecastDao
import io.rm.mvisample.core.data.remote.ApiOpenWeatherMap
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.mock.Calls

class ForecastRepositoryTest {
    lateinit var appDatabase: AppDatabase
    lateinit var forecastDao: ForecastDao
    lateinit var cityDao: CityDao
    lateinit var forecastRepositoryInput: ForecastRepositoryInput
    val cityId = 123
    val forecast = Forecast(
        id = "1",
        cityId = this.cityId.toString(),
        time = 123123,
        date = "date",
        dayOfWeek = "asd",
        tempMin = 0,
        tempMax = 0,
        pressure = 0.0,
        humidity = 0.0,
        weatherIcon = "weatherIcon",
        weatherDescription = "weatherDescription",
        windDirection = null,
        windSpeed = null
    )
    val city = City(cityId, "Novokuznetsk", "RU", 0, "icon")
    val request = RequestGetCityForecast(cityId = this.cityId.toString(), count = 1)

    @Before
    fun startUp() {
        this.appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
        this.forecastDao = this.appDatabase.forecastDao()
        this.cityDao = this.appDatabase.cityDao()
        cityDao.addCity(this.city)

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val resources = applicationContext.resources
        val safeJobExecutor = AppSafeJobExecutor(applicationContext)

        val forecastLocalRepositoryInput =
            ForecastLocalRepository(this.appDatabase, safeJobExecutor, Dispatchers.Default, resources)

        val apiOpenWeatherMap = mock(ApiOpenWeatherMap::class.java)
        `when`(
            apiOpenWeatherMap.getCityForecast(
                this.request.cityId,
                this.request.count
            )
        ).thenReturn(Calls.response(ResponseGetCityForecast(listOf(this.forecast))))

        val forecastRemoteRepositoryInput =
            ForecastRemoteRepository(apiOpenWeatherMap, safeJobExecutor, Dispatchers.IO, resources)

        this.forecastRepositoryInput = ForecastRepository(
            forecastLocalRepositoryInput,
            forecastRemoteRepositoryInput,
            safeJobExecutor,
            Dispatchers.IO,
            resources
        )
    }

    @After
    fun tearDown() {
        this.appDatabase.close()
    }

    @Test
    fun forecastInDb_getQuickForecast_returnsListForecast() {
        runBlocking {
            this@ForecastRepositoryTest.forecastRepositoryInput.updateForecast(
                request = this@ForecastRepositoryTest.request
            )

            val result = this@ForecastRepositoryTest.forecastRepositoryInput.getQuickForecast(
                request = this@ForecastRepositoryTest.request
            )

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.forecasts.isNotEmpty())
            assertTrue(result.data.forecasts[0] == this@ForecastRepositoryTest.forecast)
        }
    }

    @Test
    fun null_getQuickForecast_returnsEmptyList() {
        runBlocking {
            val result = this@ForecastRepositoryTest.forecastRepositoryInput.getQuickForecast(
                request = this@ForecastRepositoryTest.request
            )

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.forecasts.isEmpty())
        }
    }

    @Test
    fun expriredForecast_updateForecast_updatedForecast() {
        runBlocking {
            this@ForecastRepositoryTest.forecastDao.setForecast(listOf(this@ForecastRepositoryTest.forecast.copy(time = 123)))

            this@ForecastRepositoryTest.forecastRepositoryInput.updateForecast(
                request = this@ForecastRepositoryTest.request
            )

            val result =
                this@ForecastRepositoryTest.forecastRepositoryInput.getQuickForecast(this@ForecastRepositoryTest.request)

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.forecasts.isNotEmpty())
            assertTrue(result.data.forecasts[0] == this@ForecastRepositoryTest.forecast)
        }
    }
}