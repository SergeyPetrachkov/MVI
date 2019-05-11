package io.rm.mvisample.modules.splash

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.modules.citieslist.CitiesListActivity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class SplashInstrumentalTest {

    @Inject
    lateinit var citiesRepositoryInteractor: CitiesRepositoryInput

    @Test
    fun validResult_updateCities_routeToCitiesList() {

        Intents.init()

        ActivityScenario.launch(SplashActivity::class.java)

        intended(hasComponent(CitiesListActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun invalidResult_updateCities_routeToCitiesList() {
        val splashTestApplication =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as SplashTestApplication
        splashTestApplication.applicationComponent.inject(this)

        val errorText = "Some error occured!"

        runBlocking {
            `when`(this@SplashInstrumentalTest.citiesRepositoryInteractor.updateCities()).thenReturn(
                Result.Error(
                    Error(errorText)
                )
            )
        }

        Intents.init()

        ActivityScenario.launch(SplashActivity::class.java)

        intended(hasComponent(CitiesListActivity::class.java.name))

        Intents.release()
    }
}