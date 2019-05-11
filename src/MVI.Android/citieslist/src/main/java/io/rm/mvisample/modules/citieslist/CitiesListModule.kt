package io.rm.mvisample.modules.citieslist

import dagger.Module
import dagger.Provides
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.service.CoroutineService
import io.rm.mvisample.core.DefaultCoroutineExceptionHandler
import io.rm.mvisample.core.data.CitiesRepositoryInput

@Module
class CitiesListModule {

    @Provides
    fun interactorInput(citiesRepository: CitiesRepositoryInput): CitiesListInteractorInput {
        return CitiesListInteractor(citiesRepository)
    }

    @Provides
    fun presenterInput(
        moduleIn: CitiesListModuleIn,
        interactorInput: CitiesListInteractorInput,
        coroutineService: CoroutineService,
        defaultCoroutineExceptionHandler: DefaultCoroutineExceptionHandler
    ): CitiesListPresenterInput {
        val presenter = CitiesListPresenter(moduleIn, interactorInput)
        interactorInput.output = presenter
        defaultCoroutineExceptionHandler.onError = presenter::onError
        coroutineService.defaultCoroutineExceptionHandler = defaultCoroutineExceptionHandler
        interactorInput.coroutineService = coroutineService
        return presenter
    }

    @Provides
    fun routerInput(): CitiesListRouterInput {
        return CitiesListRouter()
    }

    @Provides
    fun moduleIn(activity: CitiesListActivity): CitiesListModuleIn {
        return activity.intent.getSerializableExtra(DependencyNames.MODULE_IN) as CitiesListModuleIn
    }
}