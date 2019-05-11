package io.rm.mvisample.core.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.rm.mvisample.core.data.entity.City
import java.lang.reflect.Type

class CityDeserializer : DefaultDeserializer(), JsonDeserializer<City> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): City {
        val jsonObject = json.asJsonObject

        val id = read<Int>("id", jsonObject)
        val name = read<String>("name", jsonObject)
        val country = readObjectProperty<String>("sys", "country", jsonObject)
        val temp = readObjectProperty<Double>("main", "temp", jsonObject)
        val weatherIcon = readWeatherIcon(jsonObject)

        if (id != null
            && name != null
            && country != null
            && temp != null
            && weatherIcon != null
        ) {
            return City(id, name, country, temp.toInt(), weatherIcon)
        } else {
            throw IllegalStateException()
        }
    }

    private fun readWeatherIcon(jsonObject: JsonObject): String? {
        val weatherArray = jsonObject.getAsJsonArray("weather")

        return if (weatherArray != null && weatherArray.size() > 0) {
            val weather = weatherArray[0] as JsonObject
            read<String>("icon", weather)
        } else {
            null
        }
    }
}