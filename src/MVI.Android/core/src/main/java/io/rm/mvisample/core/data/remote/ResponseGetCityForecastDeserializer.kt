package io.rm.mvisample.core.data.remote

import android.content.res.Resources
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.rm.mvisample.core.R
import io.rm.mvisample.core.data.entity.Forecast
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

class ResponseGetCityForecastDeserializer(val resources: Resources) : DefaultDeserializer(),
    JsonDeserializer<ResponseGetCityForecast> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ResponseGetCityForecast {
        val jsonObject = json.asJsonObject

        val cityId = readObjectProperty<Int>("city", "id", jsonObject)?.toString()
        val forecasts = jsonObject.getAsJsonArray("list")
            .map { jsonElement: JsonElement ->
                jsonElement.asJsonObject
            }
            .map { forecastJsonObject: JsonObject ->
                val time = read<Long>("dt", forecastJsonObject)?.let { it * 1000 }
                val date = time?.let { SimpleDateFormat("dd.MM", Locale.US).format(Date(it)) }
                val dayOfWeek = time?.let {
                    val serverDate = Calendar.getInstance()
                    serverDate.timeInMillis = it
                    val deviceDate = Calendar.getInstance()

                    when {
                        serverDate.get(Calendar.DAY_OF_YEAR) == deviceDate.get(Calendar.DAY_OF_YEAR) -> {
                            this.resources.getString(R.string.core_today)
                        }
                        else -> {
                            serverDate.getDisplayName(
                                Calendar.DAY_OF_WEEK,
                                Calendar.LONG,
                                Locale.US
                            )
                        }
                    }
                }
                val tempMin = readObjectProperty<Double>("temp", "min", forecastJsonObject)
                val tempMax = readObjectProperty<Double>("temp", "max", forecastJsonObject)
                val pressure = read<Double>("pressure", forecastJsonObject)
                val humidity = read<Double>("humidity", forecastJsonObject)
                val (weatherIcon, weatherDescription) = readWeatherIconAndDescription(
                    forecastJsonObject
                )

                val directions = arrayOf(
                    "N",
                    "NE",
                    "E",
                    "SE",
                    "S",
                    "SW",
                    "W",
                    "NW"
                )
                val windDeg = read<Double>("deg", forecastJsonObject)

                val windDirection = windDeg?.let { directions[(it / 45).toInt() % 8] }
                val windSpeed = read<Double>("speed", forecastJsonObject)?.roundToInt()

                if (cityId != null
                    && time != null
                    && date != null
                    && dayOfWeek != null
                    && tempMin != null
                    && tempMax != null
                    && pressure != null
                    && humidity != null
                    && weatherIcon != null
                    && weatherDescription != null
                    && windDirection != null
                    && windSpeed != null
                ) {
                    Forecast(
                        UUID.randomUUID().toString(),
                        cityId,
                        time,
                        date,
                        dayOfWeek,
                        Math.floor(tempMin).toInt(),
                        Math.ceil(tempMax).toInt(),
                        pressure,
                        humidity,
                        weatherIcon,
                        weatherDescription,
                        windDirection,
                        windSpeed
                    )
                } else {
                    throw IllegalStateException()
                }
            }

        if (cityId != null && forecasts.isNotEmpty()) {
            return ResponseGetCityForecast(forecasts)
        } else {
            throw IllegalStateException()
        }
    }

    private fun readWeatherIconAndDescription(jsonObject: JsonObject): Pair<String?, String?> {
        val weatherArray = jsonObject.getAsJsonArray("weather")
        val weather = weatherArray[0] as JsonObject
        return Pair(read<String>("icon", weather), read<String>("description", weather))
    }
}