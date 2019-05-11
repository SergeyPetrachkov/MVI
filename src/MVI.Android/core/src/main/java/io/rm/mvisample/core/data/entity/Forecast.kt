package io.rm.mvisample.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = arrayOf(
        ForeignKey(
            entity = City::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("cityId"),
            onDelete = ForeignKey.CASCADE
        )
    ),
    indices = arrayOf(Index("cityId"))
)
data class Forecast(
    @PrimaryKey
    val id: String,
    val cityId: String,
    val time: Long,
    val date: String,
    val dayOfWeek: String,
    val tempMin: Int,
    val tempMax: Int,
    val pressure: Double,
    val humidity: Double,
    val weatherIcon: String,
    val weatherDescription: String,
    val windDirection: String?,
    val windSpeed: Int?
)