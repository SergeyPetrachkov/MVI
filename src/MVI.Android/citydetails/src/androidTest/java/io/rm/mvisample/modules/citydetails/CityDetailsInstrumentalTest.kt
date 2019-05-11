package io.rm.mvisample.modules.citydetails

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.rm.android.testutils.AdapterCountAssertion
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.ForecastRepositoryInput
import io.rm.mvisample.core.data.entity.Forecast
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class CityDetailsInstrumentalTest {

    @Inject
    lateinit var forecastRepositoryInput: ForecastRepositoryInput

    lateinit var activityScenario: ActivityScenario<CityDetailsActivity>
    var resources: Resources

    val cityId: Int = 123
    val cityName: String = "Novokuznetsk"
    val pageSize: Int = 5

    val forecasts = listOf(
        Forecast(
            id = "1",
            cityId = this.cityId.toString(),
            time = 123123,
            date = "10.5",
            dayOfWeek = "Monday",
            tempMin = 10,
            tempMax = 20,
            pressure = 750.0,
            humidity = 60.0,
            weatherIcon = "10",
            weatherDescription = "few clouds",
            windDirection = "E",
            windSpeed = 5
        ),
        Forecast(
            id = "2",
            cityId = this.cityId.toString(),
            time = 123124,
            date = "11.5",
            dayOfWeek = "Tuesday",
            tempMin = 15,
            tempMax = 25,
            pressure = 740.0,
            humidity = 50.0,
            weatherIcon = "15",
            weatherDescription = "overcast clouds",
            windDirection = "W",
            windSpeed = 1
        )
    )

    val errorText = "Some error occured!"

    init {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        (applicationContext as CityDetailsTestApplication).applicationComponent.inject(this)
        this.resources = applicationContext.resources
    }

    @After
    fun tearDown() {
        if (this::activityScenario.isInitialized) {
            this.activityScenario.close()
        }
    }

    @Test
    fun defaultState() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        this.activityScenario.onActivity {
            assertTrue(it.title == this.resources.getString(R.string.title_city_details))
        }

        onView(withText(this.cityName)).check(matches(isDisplayed()))
        onView(
            withText(
                String.format(
                    this.resources.getString(R.string.city_details_label),
                    this.pageSize
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(AdapterCountAssertion(this.forecasts.size))
    }

    @Test
    fun defaultState_forecastItemView() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        val forecast = this.forecasts[0]

        onView(withText(forecast.dayOfWeek)).check(matches(isDisplayed()))
        onView(withText(forecast.date)).check(matches(isDisplayed()))
        onView(withText(forecast.weatherDescription)).check(matches(isDisplayed()))
        onView(withText("${if (forecast.tempMin > 0) "+" else ""}${forecast.tempMin}\u00b0/${if (forecast.tempMax > 0) "+" else ""}${forecast.tempMax}\u00b0")).check(matches(isDisplayed()))
        onView(withText(forecast.windDirection)).check(matches(isDisplayed()))
        onView(
            withText(
                String.format(
                    this.resources.getString(R.string.city_details_wind_speed),
                    forecast.windSpeed
                )
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun defaultState_tapButtonHome_finishActivity() {
        mockDataSource(isDataValid = true)

        val intent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            CityDetailsActivity::class.java
        )
        intent.putExtra(
            DependencyNames.MODULE_IN, CityDetailsModuleIn(
                cityId = this.cityId.toString(),
                cityName = this.cityName,
                pageSize = this.pageSize
            )
        )

        val activityTestRule =
            ActivityTestRule<CityDetailsActivity>(CityDetailsActivity::class.java)
        activityTestRule.launchActivity(intent)

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())

        assertTrue(activityTestRule.activity.isFinishing)

        activityTestRule.finishActivity()
    }

    @Test
    fun invalidResult_getQuickForecast_showError() {
        mockDataSource(isDataValid = false)

        launchActivityScenario()

        onView(withText(this.errorText)).check(matches(isDisplayed()))
    }

    private fun launchActivityScenario() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            CityDetailsActivity::class.java
        )
        intent.putExtra(
            DependencyNames.MODULE_IN, CityDetailsModuleIn(
                cityId = this.cityId.toString(),
                cityName = this.cityName,
                pageSize = this.pageSize
            )
        )

        this.activityScenario = ActivityScenario.launch<CityDetailsActivity>(intent)
    }

    private fun mockDataSource(isDataValid: Boolean) {
        if (isDataValid) {
            runBlocking {
                val test = this@CityDetailsInstrumentalTest
                Mockito.`when`(
                    test.forecastRepositoryInput.getQuickForecast(
                        request = RequestGetCityForecast(
                            cityId = test.cityId.toString(),
                            count = test.pageSize
                        )
                    )
                )
                    .thenReturn(
                        Result.Success(
                            data = ResponseGetCityForecast(forecasts = test.forecasts)
                        )
                    )

                Mockito.`when`(
                    test.forecastRepositoryInput.updateForecast(
                        request = RequestGetCityForecast(
                            cityId = test.cityId.toString(),
                            count = test.pageSize
                        )
                    )
                )
                    .thenReturn(
                        Result.Success(
                            data = ResponseGetCityForecast(forecasts = test.forecasts)
                        )
                    )
            }
        } else {
            runBlocking {
                val test = this@CityDetailsInstrumentalTest
                Mockito.`when`(
                    test.forecastRepositoryInput.getQuickForecast(
                        request = RequestGetCityForecast(
                            cityId = test.cityId.toString(),
                            count = test.pageSize
                        )
                    )
                )
                    .thenReturn(
                        Result.Error(Error(test.errorText))
                    )
            }
        }
    }
}