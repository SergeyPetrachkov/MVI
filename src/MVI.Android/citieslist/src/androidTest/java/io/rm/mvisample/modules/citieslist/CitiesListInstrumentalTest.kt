package io.rm.mvisample.modules.citieslist

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.verify
import io.rm.android.testutils.AdapterCountAssertion
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.Result
import io.rm.mvisample.core.Properties
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.remote.RequestAddCityByName
import io.rm.mvisample.core.data.remote.RequestRemoveCityById
import io.rm.mvisample.core.data.remote.ResponseAddCityByName
import io.rm.mvisample.core.data.remote.ResponseGetCities
import io.rm.mvisample.core.data.remote.ResponseRemoveCityById
import io.rm.mvisample.core.data.remote.ResponseUpdateCities
import io.rm.mvisample.modules.citieslist.cells.CityViewHolder
import io.rm.mvisample.modules.citydetails.CityDetailsActivity
import io.rm.mvisample.modules.citydetails.CityDetailsModuleIn
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class CitiesListInstrumentalTest {

    @Inject
    lateinit var citiesRepositoryInput: CitiesRepositoryInput

    lateinit var activityScenario: ActivityScenario<CitiesListActivity>
    var resources: Resources
    val cities =
        listOf(
            City(123, "Moscow", "RU", 2, "10"),
            City(124, "Saint-Petersburg", "RU", 4, "10"),
            City(125, "Samara", "RU", 3, "10"),
            City(126, "Rostov-na-Donu", "RU", 10, "10"),
            City(127, "Nizhniy Novgorod", "RU", 12, "10"),
            City(128, "Kazan", "RU", 20, "10"),
            City(129, "Yekaterinburg", "RU", 8, "10"),
            City(130, "Omsk", "RU", 1, "10"),
            City(131, "Novosibirsk", "RU", 20, "10"),
            City(132, "Novokuznetsk", "RU", 25, "10")
        )
    val alreadyAddedCityName = this.cities[0].name
    val cityIdForRemoving = this.cities[0].id
    val cityNameForRemoving = this.cities[0].name
    val validCityName = "Vladivostok"
    val invalidCityName = "asdf"
    val errorText = "Some error occured!"

    init {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        (applicationContext as CitiesListTestApplication).applicationComponent.inject(this)
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
            assertTrue(it.title == this.resources.getString(R.string.citieslist_title))
        }

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(AdapterCountAssertion(this.cities.size))
        onView(withId(R.id.recyclerView)).perform(scrollToPosition<CityViewHolder>(9))

        onView(withId(R.id.buttonAdd)).check(matches(isDisplayed()))
    }

    @Test
    fun citiesAreLoaded_swipeToRefresh_hitCitiesRepositoryInput() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.swipeLayout)).perform(swipeDown())

        runBlocking {
            verify(this@CitiesListInstrumentalTest.citiesRepositoryInput, atLeast(1)).getCities()
            verify(this@CitiesListInstrumentalTest.citiesRepositoryInput, atLeast(1)).updateCities()
        }
    }

    @Test
    fun citiesAreLoaded_pickItem_routeToCityDetails() {
        mockDataSource(isDataValid = true)

        val intent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            CitiesListActivity::class.java
        )
        intent.putExtra(DependencyNames.MODULE_IN, CitiesListModuleIn(Properties.PAGE_SIZE))

        val intentsTestRule = IntentsTestRule(CitiesListActivity::class.java)
        intentsTestRule.launchActivity(intent)

        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<CityViewHolder>(
                0,
                click()
            )
        )

        val desiredModuleIn = CityDetailsModuleIn(
            cityId = this.cities[0].id.toString(),
            cityName = this.cities[0].name,
            pageSize = 5
        )

        intended(
            allOf(
                hasComponent(CityDetailsActivity::class.java.name),
                hasExtra(DependencyNames.MODULE_IN, desiredModuleIn)
            )
        )

        intentsTestRule.finishActivity()
    }

    @Test
    fun citiesAreLoaded_tapFab_showDialog() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.buttonAdd)).perform(click())

        onView(withText(this.resources.getString(R.string.citieslist_title_add_city_dialog)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addCityDialogIsDisplayed_validCityName_cityIsAdded() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.buttonAdd)).perform(click())
        onView(withHint(R.string.citieslist_hint_add_city_dialog)).perform(typeText(this.validCityName))
        onView(withText(R.string.citieslist_dialog_ok)).perform(click())
        onView(withId(R.id.recyclerView)).check(AdapterCountAssertion(this.cities.size + 1))
        onView(withText("${this.validCityName}, RU")).check(matches(isDisplayed()))
    }

    @Test
    fun addCityDialogIsDisplayed_invalidCityName_error() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.buttonAdd)).perform(click())
        onView(withHint(R.string.citieslist_hint_add_city_dialog)).perform(typeText(this.invalidCityName))
        onView(withText(R.string.citieslist_dialog_ok)).perform(click())
        onView(withText(R.string.core_error_cities_repository_get_city_by_name)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun addCityDialogIsDisplayed_alreadyAddedCityName_error() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.buttonAdd)).perform(click())
        onView(withHint(R.string.citieslist_hint_add_city_dialog)).perform(typeText(this.alreadyAddedCityName))
        onView(withText(R.string.citieslist_dialog_ok)).perform(click())
        onView(withText(R.string.core_error_cities_repository_add_city_by_name)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun citiesAreLoaded_longTapOnCityItem_displayButtonDeleteAndHideButtonAdd() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<CityViewHolder>(
                0,
                longClick()
            )
        )
        onView(withId(R.id.buttonAdd)).check(matches(not(isDisplayed())))
        onView(withId(R.id.delete)).check(matches(isDisplayed()))
    }

    @Test
    fun cityItemIsSelected_tapButtonDelete_cityItemDoesNotExist_displayButtonAdd_removeButtonDelete() {
        mockDataSource(isDataValid = true)

        launchActivityScenario()

        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<CityViewHolder>(
                0,
                longClick()
            )
        )
        onView(withId(R.id.delete)).perform(click())
        onView(withText("${this.cityNameForRemoving}, RU")).check(doesNotExist())
        onView(withId(R.id.buttonAdd)).check(matches(isDisplayed()))
        onView(withId(R.id.delete)).check(doesNotExist())
    }

    @Test
    fun invalidResult_getCities_showError() {
        mockDataSource(isDataValid = false)

        launchActivityScenario()

        onView(withText(this.errorText)).check(matches(isDisplayed()))
    }

    private fun mockDataSource(isDataValid: Boolean) {
        if (isDataValid) {
            runBlocking {
                val test = this@CitiesListInstrumentalTest
                Mockito.`when`(test.citiesRepositoryInput.updateCities())
                    .thenReturn(
                        Result.Success(data = ResponseUpdateCities(test.cities))
                    )

                Mockito.`when`(test.citiesRepositoryInput.getCities())
                    .thenReturn(
                        Result.Success(data = ResponseGetCities(test.cities))
                    )

                Mockito.`when`(
                    test.citiesRepositoryInput.addCityByName(
                        request = RequestAddCityByName(test.validCityName)
                    )
                ).thenReturn(
                    Result.Success(
                        data = ResponseAddCityByName(
                            City(
                                id = 321,
                                name = test.validCityName,
                                country = "RU",
                                temp = 14,
                                weatherIcon = "10"
                            )
                        )
                    )
                )

                Mockito.`when`(
                    test.citiesRepositoryInput.addCityByName(
                        request = RequestAddCityByName(test.invalidCityName)
                    )
                ).thenReturn(
                    Result.Error(Error(test.resources.getString(R.string.core_error_cities_repository_get_city_by_name)))
                )

                Mockito.`when`(
                    test.citiesRepositoryInput.addCityByName(
                        request = RequestAddCityByName(test.alreadyAddedCityName)
                    )
                ).thenReturn(
                    Result.Error(Error(test.resources.getString(R.string.core_error_cities_repository_add_city_by_name)))
                )

                Mockito.`when`(
                    test.citiesRepositoryInput.removeCityById(
                        request = RequestRemoveCityById(
                            cityId = test.cityIdForRemoving
                        )
                    )
                ).thenReturn(Result.Success(data = ResponseRemoveCityById(test.cityIdForRemoving)))
            }
        } else {
            val test = this@CitiesListInstrumentalTest
            runBlocking {
                Mockito.`when`(test.citiesRepositoryInput.updateCities())
                    .thenReturn(
                        Result.Error(
                            Error(test.errorText)
                        )
                    )

                Mockito.`when`(test.citiesRepositoryInput.getCities())
                    .thenReturn(
                        Result.Error(
                            Error(test.errorText)
                        )
                    )
            }
        }
    }

    private fun launchActivityScenario() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            CitiesListActivity::class.java
        )
        intent.putExtra(DependencyNames.MODULE_IN, CitiesListModuleIn(Properties.PAGE_SIZE))

        this.activityScenario = ActivityScenario.launch<CitiesListActivity>(intent)
    }
}