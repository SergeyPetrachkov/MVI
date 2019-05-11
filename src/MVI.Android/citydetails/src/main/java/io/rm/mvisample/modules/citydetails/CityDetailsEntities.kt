package io.rm.mvisample.modules.citydetails

import io.rm.mvi.presenter.CollectionModuleIn
import io.rm.mvi.presenter.State
import io.rm.mvisample.core.data.entity.Forecast

class CityDetailsModuleIn(val cityId: String, val cityName: String, pageSize: Int) :
    CollectionModuleIn(pageSize) {
    override fun equals(other: Any?): Boolean {
        return other is CityDetailsModuleIn
                && other.cityId == this.cityId
                && other.cityName == this.cityName
                && other.pageSize == this.pageSize
    }

    override fun hashCode(): Int {
        return this.pageSize + this.cityId.hashCode() + this.cityName.hashCode()
    }
}

class CityDetailsState {
    class City(val cityName: String, val numDays: Int, items: MutableList<CityDetailsItem>) :
        State.CollectionItems<CityDetailsItem>(items, true)
}

sealed class CityDetailsItem {
    class Container(val forecast: Forecast) : CityDetailsItem()
    class NoResults : CityDetailsItem()
    class Pending : CityDetailsItem()
}