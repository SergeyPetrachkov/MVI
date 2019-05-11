package io.rm.mvisample.modules.citieslist

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import io.rm.mvi.view.RecyclerViewAdapter
import io.rm.mvisample.modules.citieslist.cells.CityViewHolder
import io.rm.mvisample.modules.citieslist.cells.PendingViewHolder

class CitiesListAdapter(
    context: Context,
    delegate: CitiesListDelegate
) : RecyclerViewAdapter<CitiesListItem, RecyclerViewAdapter.BindableViewHolder<CitiesListItem, CitiesListDelegate>, CitiesListDelegate>(
    context, delegate
) {
    override fun createViewHolder(
        view: View,
        viewType: Int
    ): BindableViewHolder<CitiesListItem, CitiesListDelegate> {
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        return when (viewType) {
            R.layout.citieslist_city_item -> CityViewHolder(view) as BindableViewHolder<CitiesListItem, CitiesListDelegate>
            R.layout.citieslist_pending_item -> PendingViewHolder(view) as BindableViewHolder<CitiesListItem, CitiesListDelegate>
            else -> throw IllegalStateException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (this.dataSource?.getItemAtPosition(position)) {
            is CitiesListItem.City -> R.layout.citieslist_city_item
            is CitiesListItem.Pending -> R.layout.citieslist_pending_item
            is CitiesListItem.NoResults -> R.layout.citieslist_noresults_item
            else -> throw IllegalStateException()
        }
    }
}
