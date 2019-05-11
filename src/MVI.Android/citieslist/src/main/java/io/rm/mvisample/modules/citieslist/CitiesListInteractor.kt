package io.rm.mvisample.modules.citieslist

import io.rm.mvi.interactor.Interactor
import io.rm.mvi.interactor.InteractorInput
import io.rm.mvi.interactor.InteractorOutput
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.CitiesRepositoryInput
import io.rm.mvisample.core.data.remote.RequestAddCityByName
import io.rm.mvisample.core.data.remote.RequestRemoveCityById

interface CitiesListInteractorInput :
    InteractorInput<CitiesListInteractorOutput> {
    fun addCityByName(request: RequestAddCityByName)
    fun removeCityById(request: RequestRemoveCityById)
    fun getCities()
    fun updateCities()
}

interface CitiesListInteractorOutput : InteractorOutput {
    fun onResult(result: Result<*>)
}

class CitiesListInteractor(private val citiesRepositoryInput: CitiesRepositoryInput) :
    Interactor<CitiesListInteractorOutput>(),
    CitiesListInteractorInput {

    override fun addCityByName(request: RequestAddCityByName) {
        this.launch(this::addCityByName) {
            this.output.onResult(this.citiesRepositoryInput.addCityByName(request))
        }
    }

    override fun removeCityById(request: RequestRemoveCityById) {
        this.launch(this::removeCityById) {
            this.output.onResult(this.citiesRepositoryInput.removeCityById(request))
        }
    }

    override fun getCities() {
        this.launch(this::getCities) {
            this.output.onResult(this.citiesRepositoryInput.getCities())
        }
    }

    override fun updateCities() {
        this.launch(this::updateCities) {
            this.output.onResult(this.citiesRepositoryInput.updateCities())
        }
    }
}