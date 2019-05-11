package io.rm.mvisample.modules.citydetails

import dagger.Module
import dagger.Provides
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.CoroutineService
import io.rm.mvisample.core.DefaultCoroutineExceptionHandler
import io.rm.mvisample.core.data.ForecastRepositoryInput

@Module
class CityDetailsModule {
    @Provides
    fun interactorInput(forecastRepositoryInput: ForecastRepositoryInput): CityDetailsInteractorInput {
        return CityDetailsInteractor(forecastRepositoryInput)
    }

    @Provides
    fun presenterInput(
        moduleIn: CityDetailsModuleIn,
        interactorInput: CityDetailsInteractorInput,
        coroutineService: CoroutineService,
        defaultCoroutineExceptionHandler: DefaultCoroutineExceptionHandler
    ): CityDetailsPresenterInput {
        val presenter = CityDetailsPresenter(moduleIn, interactorInput)
        interactorInput.output = presenter
        defaultCoroutineExceptionHandler.onError = presenter::onError
        coroutineService.defaultCoroutineExceptionHandler = defaultCoroutineExceptionHandler
        interactorInput.coroutineService = coroutineService
        return presenter
    }

    @Provides
    fun routerInput(): CityDetailsRouterInput {
        return CityDetailsRouter()
    }

    @Provides
    fun moduleIn(activity: CityDetailsActivity): CityDetailsModuleIn {
        return activity.intent.getSerializableExtra(DependencyNames.MODULE_IN) as CityDetailsModuleIn
    }
}