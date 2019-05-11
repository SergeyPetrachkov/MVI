package io.rm.mvisample.modules.splash

import dagger.Module
import dagger.Provides
import io.rm.mvi.presenter.ModuleIn
import io.rm.mvi.service.CoroutineService
import io.rm.mvisample.core.DefaultCoroutineExceptionHandler
import io.rm.mvisample.core.data.CitiesRepositoryInput

@Module
class SplashModule {
    @Provides
    fun interactorInput(citiesRepositoryInput: CitiesRepositoryInput): SplashInteractorInput {
        return SplashInteractor(citiesRepositoryInput)
    }

    @Provides
    fun presenterInput(
        interactorInput: SplashInteractorInput,
        coroutineService: CoroutineService,
        defaultCoroutineExceptionHandler: DefaultCoroutineExceptionHandler
    ): SplashPresenterInput {
        val presenter = SplashPresenter(ModuleIn(), interactorInput)
        interactorInput.output = presenter
        defaultCoroutineExceptionHandler.onError = presenter::onError
        coroutineService.defaultCoroutineExceptionHandler = defaultCoroutineExceptionHandler
        interactorInput.coroutineService = coroutineService
        return presenter
    }

    @Provides
    fun routerInput(): SplashRouterInput {
        return SplashRouter()
    }

    @Provides
    fun serviceInput(): SplashServiceInput {
        return SplashService()
    }
}