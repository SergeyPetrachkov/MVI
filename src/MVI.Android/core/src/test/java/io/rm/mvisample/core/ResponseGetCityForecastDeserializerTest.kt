package io.rm.mvisample.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import io.rm.mvisample.core.data.entity.Forecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecastDeserializer
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResponseGetCityForecastDeserializerTest {

    val validJson =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonCityId =
        "{\"city\":{\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonTime =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonTempMin =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonTempMax =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonPressure =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonHumidity =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonWeatherIcon =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonWeatherDescription =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"deg\":116,\"clouds\":61}]}"
    val invalidJsonWindDirection =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"speed\":1.85,\"clouds\":61}]}"
    val invalidJsonWindSpeed =
        "{\"city\":{\"id\":2172797,\"name\":\"Cairns\",\"coord\":{\"lon\":145.7667,\"lat\":-16.9167},\"country\":\"AU\",\"population\":0},\"cod\":\"200\",\"message\":2.2708353,\"cnt\":5,\"list\":[{\"dt\":1556676000,\"temp\":{\"day\":295.51,\"min\":295.51,\"max\":295.51,\"night\":295.51,\"eve\":295.51,\"morn\":295.51},\"pressure\":1014.84,\"humidity\":98,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"deg\":116,\"clouds\":61}]}"

    lateinit var gson: Gson

    val forecast = Forecast(
        id = "", //randomUUID
        cityId = "2172797",
        time = 1556676000000,
        date = "01.05",
        dayOfWeek = "Wednesday",
        tempMin = 295,
        tempMax = 296,
        pressure = 1014.84,
        humidity = 98.0,
        weatherIcon = "10n",
        weatherDescription = "light rain",
        windDirection = "E",
        windSpeed = 2
    )

    @Before
    fun startUp() {
        this.gson = GsonBuilder().registerTypeAdapter(
            ResponseGetCityForecast::class.java,
            ResponseGetCityForecastDeserializer(ApplicationProvider.getApplicationContext<Context>().resources)
        ).create()
    }

    @Test
    fun invalidJson_deserialize_validResponseGetCityForecast() {
        val responseGetCityForecast = this.gson.fromJson(this.validJson, ResponseGetCityForecast::class.java)
        assertTrue(responseGetCityForecast.forecasts.isNotEmpty())

        val deserializedForecast = responseGetCityForecast.forecasts[0]
        assertTrue(
            this.forecast.cityId == deserializedForecast.cityId
                    && this.forecast.time == deserializedForecast.time
                    && this.forecast.date == deserializedForecast.date
                    && this.forecast.dayOfWeek == deserializedForecast.dayOfWeek
                    && this.forecast.tempMax == deserializedForecast.tempMax
                    && this.forecast.tempMin == deserializedForecast.tempMin
                    && this.forecast.pressure == deserializedForecast.pressure
                    && this.forecast.humidity == deserializedForecast.humidity
                    && this.forecast.weatherDescription == deserializedForecast.weatherDescription
                    && this.forecast.weatherIcon == deserializedForecast.weatherIcon
                    && this.forecast.windDirection == deserializedForecast.windDirection
                    && this.forecast.windSpeed == deserializedForecast.windSpeed
        )
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonCityId_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonCityId, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonTime_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonTime, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonTempMin_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonTempMin, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonTempMax_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonTempMax, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonPressure_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonPressure, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonHumidity_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonHumidity, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonWeatherIcon_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonWeatherIcon, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonWeatherDescription_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonWeatherDescription, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonWindDirection_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonWindDirection, ResponseGetCityForecast::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonWindSpeed_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonWindSpeed, ResponseGetCityForecast::class.java)
    }
}