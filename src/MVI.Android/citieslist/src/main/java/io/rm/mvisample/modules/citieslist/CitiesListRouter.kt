package io.rm.mvisample.modules.citieslist

import android.app.Activity
import android.content.Intent
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.router.Router
import io.rm.mvi.router.RouterInput
import io.rm.mvisample.core.data.entity.City
import io.rm.mvisample.modules.citydetails.CityDetailsActivity
import io.rm.mvisample.modules.citydetails.CityDetailsModuleIn

interface CitiesListRouterInput : RouterInput<Activity> {
    fun showAddDialog(inputHandler: (String) -> Unit)
    fun showCityDetails(city: City)
}

class CitiesListRouter : Router<Activity>(), CitiesListRouterInput {
    override fun showAddDialog(inputHandler: (String) -> Unit) {

    }

    override fun showCityDetails(city: City) {
        this.view?.startActivity(Intent(this.view, CityDetailsActivity::class.java).also {
            it.putExtra(
                DependencyNames.MODULE_IN,
                CityDetailsModuleIn(cityId = city.id.toString(), cityName = city.name, pageSize = 5)
            )
        })
    }
}