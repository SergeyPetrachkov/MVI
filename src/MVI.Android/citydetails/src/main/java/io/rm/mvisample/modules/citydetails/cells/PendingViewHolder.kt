package io.rm.mvisample.modules.citydetails.cells

import android.view.View
import android.widget.LinearLayout
import io.rm.mvi.view.RecyclerViewAdapter

class PendingViewHolder(itemView: View) :
    RecyclerViewAdapter.BindableViewHolder<Any, Any?>(itemView = itemView) {

    override fun bindData(data: Any, delegate: Any?) {
        super.bindData(data, delegate)

        this.itemView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}