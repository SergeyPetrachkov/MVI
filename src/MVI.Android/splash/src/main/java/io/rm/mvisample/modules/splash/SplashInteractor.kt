package io.rm.mvisample.modules.splash

import io.rm.mvi.interactor.Interactor
import io.rm.mvi.interactor.InteractorInput
import io.rm.mvi.interactor.InteractorOutput
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.core.data.remote.ResponseUpdateCities

interface SplashInteractorInput : InteractorInput<SplashInteractorOutput> {
    fun updateCities()
}

interface SplashInteractorOutput : InteractorOutput {
    fun onResult(result: Result<ResponseUpdateCities>)
}

class SplashInteractor(private val citiesRepositoryInput: CitiesRepositoryInput) :
    Interactor<SplashInteractorOutput>(), SplashInteractorInput {
    override fun updateCities() {
        this.launch(this::updateCities) {
            this.output.onResult(this.citiesRepositoryInput.updateCities())
        }
    }
}