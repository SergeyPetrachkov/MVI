package io.rm.mvisample.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.rm.mvisample.core.data.entity.Forecast

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast WHERE cityId =:cityId")
    fun getForecastByCityId(cityId: String): List<Forecast>

    @Query("DELETE FROM forecast WHERE cityId = :cityId")
    fun deleteForecastByCityId(cityId: String)

    @Insert
    fun setForecast(forecast: List<Forecast>)
}