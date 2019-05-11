package io.rm.mvisample.core.data.remote

import io.rm.mvisample.core.data.entity.Forecast

data class ResponseGetCityForecast(
    val forecasts: List<Forecast>
)