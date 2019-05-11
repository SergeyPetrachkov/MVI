package io.rm.mvisample.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class City(
    @PrimaryKey val id: Int,
    val name: String,
    val country: String,
    val temp: Int,
    @ColumnInfo(name = "weather_icon") val weatherIcon: String
) : Serializable