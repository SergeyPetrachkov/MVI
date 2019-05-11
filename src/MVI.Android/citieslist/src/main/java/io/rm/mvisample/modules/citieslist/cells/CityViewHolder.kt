package io.rm.mvisample.modules.citieslist.cells

import android.annotation.SuppressLint
import android.view.View
import io.rm.mvi.view.RecyclerViewAdapter
import io.rm.mvisample.modules.citieslist.CitiesListDelegate
import io.rm.mvisample.modules.citieslist.CitiesListItem
import kotlinx.android.synthetic.main.citieslist_city_item.view.*

class CityViewHolder(itemView: View) :
    RecyclerViewAdapter.BindableViewHolder<CitiesListItem.City, CitiesListDelegate>(itemView = itemView) {

    init {
        this.itemView.setOnClickListener {
            this.delegate?.show(this.data)
        }

        this.itemView.setOnLongClickListener {
            this.delegate?.select(this.data)
            true
        }

        this.itemView.citySelection.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                this.delegate?.deselect(this.data)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(data: CitiesListItem.City, delegate: CitiesListDelegate?) {
        super.bindData(data, delegate)

        this.itemView.nameAndCountry.text = "${data.city.name}, ${data.city.country}"

        this.itemView.temp.text = "${if (data.city.temp > 0) "+" else ""}${data.city.temp}\u00b0"

        val res = this.itemView.resources.getIdentifier(
            "io.rm.vipersample:drawable/ic_" + data.city.weatherIcon.substring(0, 2),
            null,
            null
        )
        this.itemView.weatherIcon.setImageResource(res)

        if (data.isSelected) {
            this.itemView.citySelection.visibility = View.VISIBLE
            this.itemView.citySelection.isChecked = true
        } else {
            this.itemView.citySelection.visibility = View.GONE
        }
    }
}