package io.rm.mvisample.modules.citydetails

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.rm.mvi.presenter.State
import io.rm.mvi.view.RecyclerViewActivity
import io.rm.mvi.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.citydetails_activity.*

class CityDetailsActivity :
    RecyclerViewActivity<CityDetailsPresenterInput, CityDetailsRouterInput, CityDetailsItem,
            CityDetailsDelegate,
            RecyclerViewAdapter.BindableViewHolder<CityDetailsItem, CityDetailsDelegate>>(),
    CityDetailsPresenterOutput {

    override var layoutId: Int = R.layout.citydetails_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = this.resources.getString(R.string.title_city_details)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.adapter = CityDetailsAdapter(this, this.presenter)

        while (this.recyclerView.itemDecorationCount > 0) {
            this.recyclerView.removeItemDecorationAt(0)
        }

        val dividerItemDecoration = DividerItemDecoration(
            this.recyclerView.context,
            DividerItemDecoration.HORIZONTAL
        )
        this.recyclerView.addItemDecoration(dividerItemDecoration)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        this.recyclerView.layoutManager = linearLayoutManager

        this.recyclerView.adapter = this.adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        this.presenter.perform(CityDetailsAction.START)
    }

    override fun onData(data: State.Data) {
        super.onData(data)

        if (data is CityDetailsState.City) {
            this.nameAndCountry.text = data.cityName
            this.label.text = String.format(
                this.resources.getString(R.string.city_details_label),
                data.numDays
            )
        }
    }
}