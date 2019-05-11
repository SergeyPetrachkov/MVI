package io.rm.mvi.router

interface RouterInput<TypeView> {
    fun bindView(view: TypeView)
    fun unbindView()
}

open class Router<TypeView> : RouterInput<TypeView> {

    protected var view: TypeView? = null

    override fun bindView(view: TypeView) {
        this.view = view
    }

    override fun unbindView() {
        this.view = null
    }
}