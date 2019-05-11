package io.rm.mvi.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewAdapter<TypeItemModel : Any,
        TypeItemViewHolder : RecyclerViewAdapter.BindableViewHolder<TypeItemModel, TypeItemDelegate>,
        TypeItemDelegate : Any?>(
    protected var context: Context,
    protected var delegate: TypeItemDelegate?
) : RecyclerView.Adapter<TypeItemViewHolder>() {

    var dataSource: DataSource<TypeItemModel>? = null
    private var layoutInflater = LayoutInflater.from(context)

    abstract fun createViewHolder(view: View, viewType: Int): TypeItemViewHolder
    abstract override fun getItemViewType(position: Int): Int

    override fun getItemCount(): Int {
        return this.dataSource?.itemsCount ?: 0
    }

    override fun onBindViewHolder(holder: TypeItemViewHolder, position: Int) {
        this.dataSource?.let {
            holder.bindData(data = it.getItemAtPosition(position), delegate = this.delegate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeItemViewHolder {
        val view = layoutInflater.inflate(viewType, null)
        return createViewHolder(view, viewType)
    }

    open class BindableViewHolder<TypeItemModel : Any, TypeItemDelegate : Any?>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var data: TypeItemModel
        var delegate: TypeItemDelegate? = null

        open fun bindData(data: TypeItemModel, delegate: TypeItemDelegate? = null) {
            this.data = data
            this.delegate = delegate
        }
    }
}