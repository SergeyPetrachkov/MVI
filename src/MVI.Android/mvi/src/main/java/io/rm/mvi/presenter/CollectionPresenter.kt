package io.rm.mvi.presenter

import io.rm.mvi.interactor.InteractorInput

interface CollectionPresenterInput<TypeItemModel> : PresenterInput {
    fun fetch()
    fun refresh()
}

abstract class CollectionPresenter<TypeModuleIn : CollectionModuleIn,
        TypeDataState : State.CollectionItems<TypeItemModel>,
        TypePresenterOutput : PresenterOutput,
        out TypeInteractorInput : InteractorInput<*>,
        TypeItemModel>(
    moduleIn: TypeModuleIn,
    interactorInput: TypeInteractorInput
) :
    Presenter<TypeModuleIn, TypeDataState, TypePresenterOutput, TypeInteractorInput>(
        moduleIn = moduleIn,
        interactorInput = interactorInput
    ),
    CollectionPresenterInput<TypeItemModel> {

    override fun fetch() {
        if (this.state.data.isEnd.not()) {
            request()
        }
    }

    abstract fun request()

    override fun refresh() {
        request()
        this.state = this.state.copy(transient = State.Refresh())
    }

    protected inline fun <reified TypePendingItemModel : TypeItemModel, reified TypeNoResultsItemModel : TypeItemModel> onResult(
        items: ArrayList<TypeItemModel>
    ) {

        if (this.state.transient is State.Refresh) {
            this.state.data.items.clear()
            this.state.data.isEnd = true
        }

        if (items.isNotEmpty()) {
            if (items.size == this.moduleIn.pageSize) {
                items.add(
                    TypePendingItemModel::class.java.getConstructor().newInstance()
                )
            }
        } else {
            items.add(
                TypeNoResultsItemModel::class.java.getConstructor().newInstance()
            )
        }

        this.state.data.items.addAll(items)
        this.state.data.isEnd = items.size < this.moduleIn.pageSize
    }
}