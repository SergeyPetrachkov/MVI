package io.rm.mvisample.modules.citydetails

import io.rm.mvi.interactor.Interactor
import io.rm.mvi.interactor.InteractorInput
import io.rm.mvi.interactor.InteractorOutput
import io.rm.mvi.service.Result
import io.rm.mvisample.core.data.ForecastRepositoryInput
import io.rm.mvisample.core.data.remote.RequestGetCityForecast
import io.rm.mvisample.core.data.remote.ResponseGetCityForecast

interface CityDetailsInteractorInput :
    InteractorInput<CityDetailsInteractorOutput> {
    fun getForecasts(request: RequestGetCityForecast)
}

interface CityDetailsInteractorOutput : InteractorOutput {
    fun onResult(result: Result<ResponseGetCityForecast>)
}

class CityDetailsInteractor(private val forecastRepositoryInput: ForecastRepositoryInput) :
    Interactor<CityDetailsInteractorOutput>(),
    CityDetailsInteractorInput {

    override fun getForecasts(request: RequestGetCityForecast) {
        this.launch(this::getForecasts) {
            this.output.onResult(this.forecastRepositoryInput.getQuickForecast(request))
            this.output.onResult(this.forecastRepositoryInput.updateForecast(request))
        }
    }
}