package io.rm.mvisample.modules.splash

import io.rm.mvi.presenter.Action
import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.presenter.ModuleIn
import io.rm.mvi.presenter.Presenter
import io.rm.mvi.presenter.PresenterInput
import io.rm.mvi.presenter.PresenterOutput
import io.rm.mvi.presenter.State
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.remote.ResponseUpdateCities

enum class SplashAction : Action {
    REFRESH
}

interface SplashPresenterInput : PresenterInput

interface SplashPresenterOutput : PresenterOutput

class SplashPresenter(
    moduleIn: ModuleIn,
    interactor: SplashInteractorInput
) : Presenter<ModuleIn, State.Data, SplashPresenterOutput, SplashInteractorInput>(
    moduleIn,
    interactor
), SplashPresenterInput, SplashInteractorOutput {
    override var state: CombinedState<State.Data> =
        CombinedState(data = State.Data())
        set(value) {
            field = value
            this.presenterOutput?.onChangeState(value)
        }

    override fun perform(action: Action, data: Any?) {
        if (canPerform(action).not()) {
            return
        }

        when (action) {
            SplashAction.REFRESH -> {
                this.state = this.state.copy(transient = State.Pending())
                this.interactorInput.updateCities()
            }
        }
    }

    override fun onResult(result: Result<ResponseUpdateCities>) {
        this.state = this.state.copy(transient = RoutingToCitiesListState())
    }
}