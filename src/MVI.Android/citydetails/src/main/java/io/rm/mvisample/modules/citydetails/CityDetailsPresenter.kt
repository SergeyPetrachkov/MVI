package io.rm.mvisample.modules.citydetails

import io.rm.mvi.presenter.Action
import io.rm.mvi.presenter.CollectionPresenter
import io.rm.mvi.presenter.CollectionPresenterInput
import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.presenter.PresenterOutput
import io.rm.mvi.presenter.State
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast

enum class CityDetailsAction : Action {
    START
}

interface CityDetailsPresenterInput : CollectionPresenterInput<CityDetailsItem>,
    CityDetailsDelegate

interface CityDetailsPresenterOutput : PresenterOutput

class CityDetailsPresenter(
    moduleIn: CityDetailsModuleIn,
    interactor: CityDetailsInteractorInput
) : CollectionPresenter<CityDetailsModuleIn, CityDetailsState.City, CityDetailsPresenterOutput, CityDetailsInteractorInput, CityDetailsItem>(
    moduleIn,
    interactor
), CityDetailsPresenterInput, CityDetailsInteractorOutput {

    override var state: CombinedState<CityDetailsState.City> =
        CombinedState(
            CityDetailsState.City(
                this.moduleIn.cityName, this.moduleIn.pageSize, mutableListOf()
            )
        )
        set(value) {
            field = value
            this.presenterOutput?.onChangeState(value)
        }

    override fun perform(action: Action, data: Any?) {
        if (canPerform(action).not()) {
            return
        }

        when (action) {
            CityDetailsAction.START -> {
                refresh()
            }
        }
    }

    override fun request() {
        this.state = this.state.copy(transient = State.Pending())
        this.interactorInput.getForecasts(
            request = RequestGetCityForecast(
                this.moduleIn.cityId,
                this.moduleIn.pageSize
            )
        )
    }

    override fun onResult(result: Result<ResponseGetCityForecast>) {
        when (result) {
            is Result.Success -> {
                val items = mutableListOf<CityDetailsItem>()

                result.data.forecasts.map {
                    CityDetailsItem.Container(it)
                }.toCollection(items)

                this.state =
                    CombinedState(
                        data = CityDetailsState.City(
                            this.moduleIn.cityName,
                            this.moduleIn.pageSize,
                            items
                        )
                    )
            }
            is Result.Error -> {
                this.onError(result.throwable)
            }
        }
    }
}

interface CityDetailsDelegate