package io.rm.mvisample.core

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.*
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.entity.Forecast
import io.rm.mvisample.core.data.local.AppDatabase
import io.rm.mvisample.core.data.local.CityDao
import io.rm.mvisample.core.data.local.ForecastDao
import io.rm.mvisample.core.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.mock.Calls

class CitiesRepositoryTest {

    lateinit var appDatabase: AppDatabase
    lateinit var forecastDao: ForecastDao
    lateinit var cityDao: CityDao
    lateinit var citiesRepository: CitiesRepositoryInput
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
    val updatedCity = City(cityId, "Novokuznetsk", "RU", 1, "icon")
    val request = RequestGetCityForecast(cityId = this.cityId.toString(), count = 1)

    @Before
    fun startUp() {
        this.appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
        this.forecastDao = this.appDatabase.forecastDao()
        this.cityDao = this.appDatabase.cityDao()

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val resources = applicationContext.resources
        val safeJobExecutor = AppSafeJobExecutor(applicationContext)

        val citiesRepositoryLocalInput =
            CitiesRepositoryLocal(this.appDatabase, safeJobExecutor, Dispatchers.Default, resources)

        val apiOpenWeatherMap = Mockito.mock(ApiOpenWeatherMap::class.java)
        `when`(
            apiOpenWeatherMap.getCityForecast(
                this.request.cityId,
                this.request.count
            )
        ).thenReturn(Calls.response(ResponseGetCityForecast(listOf(this.forecast))))


        val citiesIds =
            InstrumentationRegistry.getInstrumentation().targetContext.resources.getIntArray(R.array.default_cities)
                .joinToString(separator = ",")

        `when`(
            apiOpenWeatherMap.getCities(citiesIds)
        ).thenReturn(Calls.response(ResponseGetCities(listOf(this.updatedCity))))

        `when`(
            apiOpenWeatherMap.getCities(this.cityId.toString())
        ).thenReturn(Calls.response(ResponseGetCities(listOf(this.updatedCity))))

        `when`(
            apiOpenWeatherMap.getCityByName(this.city.name)
        ).thenReturn(Calls.response(this.city))

        val citiesRepositoryRemoteIntput =
            CitiesRepositoryRemote(apiOpenWeatherMap, safeJobExecutor, Dispatchers.IO, resources)

        this.citiesRepository = CitiesRepository(
            citiesRepositoryLocalInput,
            citiesRepositoryRemoteIntput,
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
    fun citiesInDb_getCities_returnsCitiesFromDb() {
        this.cityDao.addCity(this.city)

        runBlocking {
            val result = this@CitiesRepositoryTest.citiesRepository.getCities()

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.list.isNotEmpty())
            assertTrue(result.data.list[0] == this@CitiesRepositoryTest.city)
        }
    }

    @Test
    fun null_getCities_returnsDefaultList() {
        runBlocking {
            val result = this@CitiesRepositoryTest.citiesRepository.getCities()

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.list.isNotEmpty())
            assertTrue(result.data.list[0] == this@CitiesRepositoryTest.updatedCity)
        }
    }

    @Test
    fun validName_addCityByName_cityAddedToDb() {
        runBlocking {
            val result =
                this@CitiesRepositoryTest.citiesRepository.addCityByName(
                    request = RequestAddCityByName(this@CitiesRepositoryTest.city.name)
                )

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.city == this@CitiesRepositoryTest.city)
        }
    }

    @Test
    fun invalidName_addCityByName_error() {
        runBlocking {
            val result =
                this@CitiesRepositoryTest.citiesRepository.addCityByName(
                    request = RequestAddCityByName("asdf")
                )

            assertTrue(result is Result.Error)
        }
    }

    @Test
    fun cityIsInDb_addCityByName_error() {
        this.cityDao.addCity(this.city)

        runBlocking {
            val result =
                this@CitiesRepositoryTest.citiesRepository.addCityByName(RequestAddCityByName(this@CitiesRepositoryTest.city.name))

            assertTrue(result is Result.Error)
            assertTrue(
                (result as Result.Error).throwable.message == InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(
                    R.string.core_error_cities_repository_add_city_by_name
                )
            )
        }
    }

    @Test
    fun cityIsInDb_removeCityById_cityIsNotInDb() {
        this.cityDao.addCity(this.city)

        runBlocking {
            this@CitiesRepositoryTest.citiesRepository.removeCityById(
                request = RequestRemoveCityById(this@CitiesRepositoryTest.cityId)
            )

            val citiesList = this@CitiesRepositoryTest.cityDao.getCities()

            assertTrue(citiesList.isEmpty())
        }
    }

    @Test
    fun oldCityData_updateCities_updatedCityData() {
        this.cityDao.addCity(this.city)

        runBlocking {
            this@CitiesRepositoryTest.citiesRepository.updateCities()

            val result = this@CitiesRepositoryTest.citiesRepository.getCities()

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.list.isNotEmpty())
            assertTrue(result.data.list[0] == this@CitiesRepositoryTest.updatedCity)
        }
    }
}