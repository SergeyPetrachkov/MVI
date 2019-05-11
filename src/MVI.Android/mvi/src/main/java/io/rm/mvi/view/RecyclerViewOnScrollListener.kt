package io.rm.mvi.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.rm.mvi.presenter.CollectionPresenterInput

class RecyclerViewOnScrollListener<TypePresenter : CollectionPresenterInput<*>>(
    private val linearLayoutManager: LinearLayoutManager,
    private val presenter: TypePresenter,
    private val pageSize: Int
) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = this.linearLayoutManager.childCount
        val totalItemCount = this.linearLayoutManager.itemCount
        val firstVisibleItemPosition =
            this.linearLayoutManager.findFirstVisibleItemPosition()

        if ((visibleItemCount + firstVisibleItemPosition >= totalItemCount)
            && firstVisibleItemPosition >= 0 &&
            totalItemCount >= pageSize
        ) {
            this.presenter.fetch()
        }
    }
}