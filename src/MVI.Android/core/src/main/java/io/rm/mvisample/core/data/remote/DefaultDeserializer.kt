package io.rm.mvisample.core.data.remote

import com.google.gson.JsonObject

open class DefaultDeserializer {
    inline fun <reified TypeProperty> readObjectProperty(
        objectName: String,
        propertyName: String,
        parentObject: JsonObject
    ): TypeProperty? {
        val childObject = parentObject.get(objectName) as? JsonObject
        return childObject?.let {
            read<TypeProperty>(propertyName, it)
        }
    }

    inline fun <reified TypeProperty> read(
        propertyName: String,
        jsonObject: JsonObject
    ): TypeProperty? {
        val jsonElement = jsonObject.get(propertyName)

        val value: Any? = when (TypeProperty::class.java) {
            String::class.java -> jsonElement?.asString
            Int::class.javaObjectType -> jsonElement?.asInt
            Double::class.javaObjectType -> jsonElement?.asDouble
            Long::class.javaObjectType -> jsonElement?.asLong
            else -> throw IllegalStateException()
        }

        return value as? TypeProperty
    }
}