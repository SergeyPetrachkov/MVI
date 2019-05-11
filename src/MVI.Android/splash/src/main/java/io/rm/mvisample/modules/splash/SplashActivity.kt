package io.rm.mvisample.modules.splash

import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.view.Activity

class SplashActivity : Activity<SplashPresenterInput, SplashRouterInput>(), SplashPresenterOutput {

    override var layoutId: Int = R.layout.splash_activity

    override fun onResume() {
        super.onResume()
        this.presenter.perform(SplashAction.REFRESH)
    }

    override fun onChangeState(state: CombinedState<*>) {
        super.onChangeState(state)

        when {
            state.transient is RoutingToCitiesListState -> {
                this.router.showCitiesList(state.transient as RoutingToCitiesListState)
            }
        }
    }
}