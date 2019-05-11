package io.rm.mvisample.core.data.remote

import android.content.res.Resources
import com.google.gson.GsonBuilder
import io.rm.mvisample.core.data.entity.City
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*https://openweathermap.org/api*/
interface ApiOpenWeatherMap {
    /*http://api.openweathermap.org/data/2.5/weather?q=London*/
    @GET("weather")
    abstract fun getCityByName(@Query("q") cityName: String): Call<City>

    /*Current weather
    http://api.openweathermap.org/data/2.5/group?id=524901,703448,2643743&units=metric*/
    @GET("group")
    fun getCities(@Query("id") citiesIds: String): Call<ResponseGetCities>

    /*http://api.openweathermap.org/data/2.5/forecast/daily?id=2172797&cnt=5*/
    @GET("forecast/daily")
    fun getCityForecast(@Query("id") cityId: String, @Query("cnt") cnt: Int): Call<ResponseGetCityForecast>
}

class OpenWeatherMap (val resources: Resources) {
    private val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(City::class.java, CityDeserializer())
        .registerTypeAdapter(ResponseGetCityForecast::class.java, ResponseGetCityForecastDeserializer(this.resources))
        .create()

    private val okHttpClient = OkHttpClient().newBuilder().addInterceptor {
        val originalRequest = it.request()
        val originalHttpUrl = originalRequest.url()
        val newHttpUrl = originalHttpUrl.newBuilder()
            .addQueryParameter(
                "APPID",
                "0b357565301f6ab99a45137cb8c607d9"
            )
            .addQueryParameter("units", "metric")
            .build()

        val builder = originalRequest.newBuilder().url(newHttpUrl)
        val newRequest = builder.build()

        it.proceed(newRequest)
    }.build()

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .client(this.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(this.gson))
            .build()
    val api: ApiOpenWeatherMap = this.retrofit.create(ApiOpenWeatherMap::class.java)
}