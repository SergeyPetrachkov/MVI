package io.rm.mvisample.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.rm.mvisample.core.data.entity.City

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getCities(): List<City>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCities(cities: List<City>)

    @Insert
    fun addCity(city: City)

    @Query("DELETE FROM city WHERE id = :cityId")
    fun deleteCityById(cityId: Int)

    @Query("DELETE FROM city")
    fun deleteAll()
}