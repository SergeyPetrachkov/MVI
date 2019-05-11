package io.rm.mvisample.modules.splash

import android.app.Activity
import android.content.Intent
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.router.Router
import io.rm.mvi.router.RouterInput
import io.rm.mvisample.core.Properties
import io.rm.mvisample.modules.citieslist.CitiesListActivity
import io.rm.mvisample.modules.citieslist.CitiesListModuleIn

interface SplashRouterInput : RouterInput<Activity> {
    fun showCitiesList(routingToCitiesListState: RoutingToCitiesListState)
}

class SplashRouter : Router<Activity>(), SplashRouterInput {
    override fun showCitiesList(routingToCitiesListState: RoutingToCitiesListState) {
        this.view?.startActivity(Intent(this.view, CitiesListActivity::class.java).also {
            it.putExtra(
                DependencyNames.MODULE_IN,
                CitiesListModuleIn(pageSize = Properties.PAGE_SIZE)
            )
        })
        this.view?.finish()
    }
}