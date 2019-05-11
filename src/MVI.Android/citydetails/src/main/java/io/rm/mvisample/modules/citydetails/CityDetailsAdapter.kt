package io.rm.mvisample.modules.citydetails

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import io.rm.mvi.view.RecyclerViewAdapter
import io.rm.mvisample.modules.citydetails.cells.ForecastViewHolder
import io.rm.mvisample.modules.citydetails.cells.PendingViewHolder

class CityDetailsAdapter(
    context: Context,
    delegate: CityDetailsDelegate
) : RecyclerViewAdapter<CityDetailsItem, RecyclerViewAdapter.BindableViewHolder<CityDetailsItem, CityDetailsDelegate>, CityDetailsDelegate>(
    context, delegate
) {
    override fun createViewHolder(
        view: View,
        viewType: Int
    ): BindableViewHolder<CityDetailsItem, CityDetailsDelegate> {
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        return when (viewType) {
            R.layout.citydetails_forecast_item -> ForecastViewHolder(view) as BindableViewHolder<CityDetailsItem, CityDetailsDelegate>
            R.layout.citydetails_noresults_item -> PendingViewHolder(view) as BindableViewHolder<CityDetailsItem, CityDetailsDelegate>
            R.layout.citydetails_pending_item -> PendingViewHolder(view) as BindableViewHolder<CityDetailsItem, CityDetailsDelegate>
            else -> throw IllegalStateException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.dataSource?.getItemAtPosition(position)) {
            is CityDetailsItem.Container -> R.layout.citydetails_forecast_item
            is CityDetailsItem.Pending -> R.layout.citydetails_pending_item
            is CityDetailsItem.NoResults -> R.layout.citydetails_noresults_item
            else -> throw IllegalStateException()
        }
    }
}
