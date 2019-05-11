package io.rm.mvisample.modules.citydetails.cells

import android.annotation.SuppressLint
import android.view.View
import io.rm.mvi.view.RecyclerViewAdapter
import io.rm.mvisample.modules.citydetails.CityDetailsDelegate
import io.rm.mvisample.modules.citydetails.CityDetailsItem
import io.rm.mvisample.modules.citydetails.R
import kotlinx.android.synthetic.main.citydetails_forecast_item.view.*

class ForecastViewHolder(itemView: View) :
    RecyclerViewAdapter.BindableViewHolder<CityDetailsItem.Container, CityDetailsDelegate>(itemView = itemView) {

    @SuppressLint("SetTextI18n")
    override fun bindData(data: CityDetailsItem.Container, delegate: CityDetailsDelegate?) {
        super.bindData(data, delegate)

        this.itemView.dayOfWeek.text = data.forecast.dayOfWeek
        this.itemView.date.text = data.forecast.date
        val res = this.itemView.resources.getIdentifier(
            "io.rm.vipersample:drawable/ic_" + data.forecast.weatherIcon.substring(0, 2),
            null,
            null
        )
        this.itemView.weatherIcon.setImageResource(res)

        this.itemView.weatherDescription.text = data.forecast.weatherDescription

        this.itemView.temp.text =
            "${if (data.forecast.tempMin > 0) "+" else ""}${data.forecast.tempMin}\u00b0/${if (data.forecast.tempMax > 0) "+" else ""}${data.forecast.tempMax}\u00b0"

        if (data.forecast.windDirection != null && data.forecast.windSpeed != null) {
            this.itemView.windDirection.text = data.forecast.windDirection
            this.itemView.windSpeed.text = String.format(
                this.itemView.resources.getString(R.string.city_details_wind_speed),
                data.forecast.windSpeed
            )
        }
    }
}