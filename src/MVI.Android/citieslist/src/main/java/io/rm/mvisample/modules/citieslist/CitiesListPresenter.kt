package io.rm.mvisample.modules.citieslist

import io.rm.mvi.presenter.Action
import io.rm.mvi.presenter.CollectionPresenter
import io.rm.mvi.presenter.CollectionPresenterInput
import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.presenter.PresenterOutput
import io.rm.mvi.presenter.State
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.remote.RequestAddCityByName
import io.rm.mvisample.core.data.remote.RequestRemoveCityById
import io.rm.mvisample.core.data.remote.ResponseAddCityByName
import io.rm.mvisample.core.data.remote.ResponseGetCities
import io.rm.mvisample.core.data.remote.ResponseRemoveCityById
import io.rm.mvisample.core.data.remote.ResponseUpdateCities

enum class CitiesListAction : Action {
    REFRESH,
    GET_CITIES,
    ADD,
    REMOVE,
    SELECT,
    DESELECT,
    SHOW
}

interface CitiesListPresenterInput : CollectionPresenterInput<CitiesListItem>, CitiesListDelegate

interface CitiesListPresenterOutput : PresenterOutput

class CitiesListPresenter(
    moduleIn: CitiesListModuleIn,
    interactor: CitiesListInteractorInput
) : CollectionPresenter<CitiesListModuleIn, State.CollectionItems<CitiesListItem>, CitiesListPresenterOutput, CitiesListInteractorInput, CitiesListItem>(
    moduleIn,
    interactor
), CitiesListPresenterInput, CitiesListInteractorOutput {

    override var state: CombinedState<State.CollectionItems<CitiesListItem>> =
        CombinedState(
            data = State.CollectionItems(mutableListOf(), true)
        )
        set(value) {
            field = value
            this.presenterOutput?.onChangeState(value)
        }

    override fun canPerform(action: Action): Boolean {
        if (!super.canPerform(action)) {
            return false
        }

        return when (this.state.transient) {
            is CitiesListState.SelectCity -> action == CitiesListAction.REMOVE || action == CitiesListAction.DESELECT
            else -> true
        }
    }

    override fun perform(action: Action, data: Any?) {
        if (canPerform(action).not()) {
            return
        }

        when (action) {
            CitiesListAction.GET_CITIES -> {
                request()
            }
            CitiesListAction.REFRESH -> {

            }
            CitiesListAction.ADD -> {
                reduce(State.Pending())
                this.interactorInput.addCityByName(request = RequestAddCityByName(data as String))
            }
            CitiesListAction.REMOVE -> {
                if (this.state.transient is CitiesListState.SelectCity
                ) {
                    val position = (this.state.transient as CitiesListState.SelectCity).position
                    val item = this.state.data.items[position]

                    if (item is CitiesListItem.City) {
                        reduce(State.Pending())
                        this.interactorInput.removeCityById(request = RequestRemoveCityById(cityId = item.city.id))
                    }
                }
            }
            CitiesListAction.SELECT -> {
                val city = data as CitiesListItem.City
                city.isSelected = true
                reduce(
                    CitiesListState.SelectCity(
                        position = this.state.data.items.indexOf(city)
                    )
                )
            }
            CitiesListAction.DESELECT -> {
                val city = data as CitiesListItem.City
                city.isSelected = false
                reduce(
                    CitiesListState.DeselectCity(
                        position = this.state.data.items.indexOf(city)
                    )
                )
            }
            CitiesListAction.SHOW -> {
                reduce(CitiesListState.ShowCity(data as CitiesListItem.City))
            }
        }
    }

    fun reduce(state: State) {
        this.state = when (state) {
            is State.CollectionItems<*> -> this.state.copy(data = state as State.CollectionItems<CitiesListItem>)
            is State.Transient -> this.state.copy(transient = state)
            is State.Error -> this.state.copy(error = state)
            else -> throw IllegalStateException()
        }
    }

    override fun request() {
        reduce(State.Pending())
        this.interactorInput.getCities()
    }

    override fun onResult(result: Result<*>) {
        when (result) {
            is Result.Success -> {
                when (val data = result.data) {
                    is ResponseGetCities -> {
                        super.onResult<CitiesListItem.Pending, CitiesListItem.NoResults>(
                            items = ArrayList(data.list.map {
                                CitiesListItem.City(
                                    city = it
                                )
                            })
                        )
                        reduce(State.Refresh())
                        this.interactorInput.updateCities()
                    }
                    is ResponseUpdateCities -> {
                        val updatedCollectionItems = mutableListOf<CitiesListItem>()

                        data.list.map {
                            CitiesListItem.City(city = it)
                        }.toCollection(updatedCollectionItems)

                        this.state = CombinedState(
                            data = State.CollectionItems(
                                items = updatedCollectionItems,
                                isEnd = true
                            )
                        )
                    }
                    is ResponseAddCityByName -> {
                        this.state.data.items.add(CitiesListItem.City(data.city))
                        reduce(CitiesListState.AddNewCity(this.state.data.items))
                    }

                    is ResponseRemoveCityById -> {
                        val item =
                            this.state.data.items.firstOrNull { it is CitiesListItem.City && it.city.id == data.cityId }
                        val position = this.state.data.items.indexOf(item)
                        reduce(CitiesListState.RemoveCity(position))
                    }
                }
            }
            is Result.Error -> {
                this.onError(result.throwable)
            }
        }
    }

    override fun show(data: CitiesListItem.City) {
        this.perform(CitiesListAction.SHOW, data)
    }

    override fun select(data: CitiesListItem.City) {
        this.perform(CitiesListAction.SELECT, data)
    }

    override fun deselect(data: CitiesListItem.City) {
        this.perform(CitiesListAction.DESELECT, data)
    }
}

interface CitiesListDelegate {
    fun show(data: CitiesListItem.City)
    fun select(data: CitiesListItem.City)
    fun deselect(data: CitiesListItem.City)
}