package io.rm.mvisample.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.core.data.entity.Forecast

@Database(entities = arrayOf(City::class, Forecast::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun forecastDao(): ForecastDao
}