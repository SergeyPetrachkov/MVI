package io.rm.mvisample.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.remote.CityDeserializer
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CityDeserializerTest {

    val validJson =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"stations\",\"main\":{\"temp\":300.15,\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"country\":\"AU\",\"sunrise\":1485720272,\"sunset\":1485766550},\"id\":2172797,\"name\":\"Cairns\",\"cod\":200}"
    val invalidJsonWeatherIcon =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\"],\"base\":\"stations\",\"main\":{\"temp\":300.15,\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"country\":\"AU\",\"sunrise\":1485720272,\"sunset\":1485766550},\"id\":2172797,\"name\":\"Cairns\",\"cod\":200}"
    val invalidJsonId =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"stations\",\"main\":{\"temp\":300.15,\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"country\":\"AU\",\"sunrise\":1485720272,\"sunset\":1485766550},\"name\":\"Cairns\",\"cod\":200}"
    val invalidJsonName =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"stations\",\"main\":{\"temp\":300.15,\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"country\":\"AU\",\"sunrise\":1485720272,\"sunset\":1485766550},\"id\":2172797,\"cod\":200}"
    val invalidJsonCountry =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"stations\",\"main\":{\"temp\":300.15,\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"sunrise\":1485720272,\"sunset\":1485766550},\"id\":2172797,\"name\":\"Cairns\",\"cod\":200}"
    val invalidJsonTemp =
        "{\"coord\":{\"lon\":145.77,\"lat\":-16.92},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"base\":\"stations\",\"main\":{\"pressure\":1007,\"humidity\":74,\"temp_min\":300.15,\"temp_max\":300.15},\"visibility\":10000,\"wind\":{\"speed\":3.6,\"deg\":160},\"clouds\":{\"all\":40},\"dt\":1485790200,\"sys\":{\"type\":1,\"id\":8166,\"message\":0.2064,\"country\":\"AU\",\"sunrise\":1485720272,\"sunset\":1485766550},\"id\":2172797,\"name\":\"Cairns\",\"cod\":200}"

    lateinit var gson: Gson

    @Before
    fun startUp() {
        this.gson = GsonBuilder().registerTypeAdapter(City::class.java, CityDeserializer()).create()
    }

    @Test
    fun validJson_deserialize_validCity() {
        val city = this.gson.fromJson(this.validJson, City::class.java)
        assertTrue(city.id.toString() == "2172797")
        assertTrue(city.name == "Cairns")
        assertTrue(city.country == "AU")
        assertTrue(city.temp == 300)
        assertTrue(city.weatherIcon == "03n")

        assertTrue(city != null)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonId_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonId, City::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonCountry_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonCountry, City::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonTemp_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonTemp, City::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonName_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonName, City::class.java)
    }

    @Test(expected = JsonSyntaxException::class)
    fun invalidJsonWeatherIcon_deserialize_exception() {
        this.gson.fromJson(this.invalidJsonWeatherIcon, City::class.java)
    }
}