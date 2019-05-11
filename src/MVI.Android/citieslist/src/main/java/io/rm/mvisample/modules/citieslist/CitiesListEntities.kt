package io.rm.mvisample.modules.citieslist

import io.rm.mvi.presenter.CollectionModuleIn
import io.rm.mvi.presenter.State
import io.rm.mvi.view.DataSource

class CitiesListModuleIn(pageSize: Int) : CollectionModuleIn(pageSize)

class CitiesListState {
    class SelectCity(val position: Int) : State.Transient()

    class DeselectCity(val position: Int) : State.Transient()

    class AddNewCity(val items: List<CitiesListItem>) : State.Transient(),
        DataSource<CitiesListItem> {
        override val itemsCount: Int
            get() = this.items.size

        override fun getItemAtPosition(position: Int): CitiesListItem {
            return this.items[position]
        }
    }

    class RemoveCity(val position: Int) : State.Transient()

    class ShowCity(val item: CitiesListItem.City) : State.Transient()
}

sealed class CitiesListItem {
    class City(val city: io.rm.mvisample.core.data.entity.City, var isSelected: Boolean = false) : CitiesListItem()
    class NoResults : CitiesListItem()
    class Pending : CitiesListItem()
}