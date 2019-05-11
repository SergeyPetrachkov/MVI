package io.rm.mvi.view

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.rm.mvi.di.DependencyNames
import io.rm.mvi.presenter.CollectionModuleIn
import io.rm.mvi.presenter.CollectionPresenterInput
import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.presenter.State
import io.rm.mvi.router.RouterInput

abstract class RecyclerViewActivity<TypePresenter : CollectionPresenterInput<*>,
        TypeRouter : RouterInput<android.app.Activity>,
        TypeItemModel : Any,
        TypeItemDelegate,
        TypeItemViewHolder : RecyclerViewAdapter.BindableViewHolder<TypeItemModel, TypeItemDelegate>> :
    Activity<TypePresenter, TypeRouter>() {
    protected lateinit var recyclerView: RecyclerView
    protected var swipeLayout: SwipeRefreshLayout? = null
    lateinit var adapter: RecyclerViewAdapter<TypeItemModel, TypeItemViewHolder, TypeItemDelegate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moduleIn =
            this.intent.getSerializableExtra(DependencyNames.MODULE_IN) as CollectionModuleIn

        val identifier = resources.getIdentifier(
            "recyclerView",
            "id",
            this.packageName
        )
        if (identifier != 0)
            this.recyclerView = this.findViewById(identifier)
        if (!::recyclerView.isInitialized)
            throw IllegalStateException()
        this.swipeLayout = this.findViewById(
            resources.getIdentifier(
                "swipeLayout",
                "id",
                this.packageName
            )
        )

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        this.recyclerView.layoutManager = linearLayoutManager

        this.recyclerView.setHasFixedSize(true)

        val dividerItemDecoration = DividerItemDecoration(
            this.recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        this.recyclerView.addItemDecoration(dividerItemDecoration)

        this.recyclerView.addOnScrollListener(
            RecyclerViewOnScrollListener(
                linearLayoutManager = linearLayoutManager,
                presenter = this.presenter,
                pageSize = moduleIn.pageSize
            )
        )

        this.swipeLayout?.setOnRefreshListener {
            this.presenter.refresh()
        }
    }

    override fun onChangeState(state: CombinedState<*>) {
        super.onChangeState(state)
    }

    override fun onData(data: State.Data) {
        super.onData(data)

        if (data is State.CollectionItems<*>) {
            this.adapter.dataSource =
                data as State.CollectionItems<TypeItemModel>
            this.adapter.notifyDataSetChanged()
        }
    }

    override fun onPending() {
        if (this.adapter.itemCount == 0) {
            this.swipeLayout?.isRefreshing = true
        }
    }

    override fun onPendingEnd() {
        this.swipeLayout?.isRefreshing = false
    }
}