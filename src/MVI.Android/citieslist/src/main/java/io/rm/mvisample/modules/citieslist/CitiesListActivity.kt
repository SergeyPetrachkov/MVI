package io.rm.mvisample.modules.citieslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.rm.mvi.presenter.State
import io.rm.mvi.view.RecyclerViewActivity
import io.rm.mvi.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.citieslist_activity.*

class CitiesListActivity :
    RecyclerViewActivity<CitiesListPresenterInput, CitiesListRouterInput, CitiesListItem,
            CitiesListDelegate,
            RecyclerViewAdapter.BindableViewHolder<CitiesListItem, CitiesListDelegate>>(),
    CitiesListPresenterOutput, CitiesDialogListener {

    override var layoutId: Int = R.layout.citieslist_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = this.getString(R.string.citieslist_title)

        this.adapter = CitiesListAdapter(this, this.presenter)
        this.recyclerView.adapter = this.adapter

        this.buttonAdd.setOnClickListener {
            val dialogFragment = CitiesDialogFragment()
            dialogFragment.show(this.supportFragmentManager, "dialog")
        }

        this.presenter.perform(CitiesListAction.GET_CITIES)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        if (this.isSelectionState) {
            this.menuInflater.inflate(R.menu.delete_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                this.presenter.perform(CitiesListAction.REMOVE)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onPositiveAction(cityName: String) {
        this.presenter.perform(CitiesListAction.ADD, cityName)
    }

    private var isSelectionState: Boolean = false

    override fun onTransient(transient: State.Transient) {
        super.onTransient(transient)

        when (transient) {
            is CitiesListState.AddNewCity -> {
                this.adapter.dataSource = transient
                this.adapter.notifyItemInserted(transient.items.size - 1)
                this.recyclerView.smoothScrollToPosition(transient.items.size - 1)
            }
            is CitiesListState.RemoveCity -> {
                this.swipeLayout?.isEnabled = true
                this.buttonAdd.show()
                this.adapter.notifyItemRemoved(transient.position)
                this.isSelectionState = false
                invalidateOptionsMenu()
            }
            is CitiesListState.SelectCity -> {
                this.swipeLayout?.isEnabled = false
                this.buttonAdd.hide()
                this.adapter.notifyItemChanged(transient.position)
                this.isSelectionState = true
                invalidateOptionsMenu()
            }
            is CitiesListState.DeselectCity -> {
                this.swipeLayout?.isEnabled = true
                this.buttonAdd.show()
                this.adapter.notifyItemChanged(transient.position)
                this.isSelectionState = false
                invalidateOptionsMenu()
            }
            is CitiesListState.ShowCity -> {
                this.router.showCityDetails(transient.item.city)
            }
        }
    }
}