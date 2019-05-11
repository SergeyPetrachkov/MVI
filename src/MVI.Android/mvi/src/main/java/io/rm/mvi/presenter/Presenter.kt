package io.rm.mvi.presenter

import io.rm.mvi.interactor.InteractorInput
import io.rm.mvi.interactor.InteractorOutput

interface Action

interface PresenterInput {
    fun bindOutput(presenterOutput: PresenterOutput)
    fun unbindOutput()
    fun perform(action: Action, data: Any? = null)
}

interface PresenterOutput {
    fun onChangeState(state: CombinedState<*>)
}

abstract class Presenter<out TypeModuleIn : ModuleIn, TypeDataState : State.Data, TypePresenterOutput : PresenterOutput, out TypeInteractorInput : InteractorInput<*>>(
    protected val moduleIn: TypeModuleIn,
    protected val interactorInput: TypeInteractorInput
) : PresenterInput, InteractorOutput {

    protected var presenterOutput: TypePresenterOutput? = null
    protected abstract var state: CombinedState<TypeDataState>

    override fun bindOutput(presenterOutput: PresenterOutput) {
        try {
            @Suppress("UNCHECKED_CAST")
            this.presenterOutput = presenterOutput as TypePresenterOutput

            if (this.state.transient != null && this.state.error != null) {
                this.presenterOutput?.onChangeState(this.state.copy(data = this.state.data))
            }

        } catch (e: ClassCastException) {
            throw IllegalStateException("presenterOutput must be TypePresenterOutput")
        }

        this.presenterOutput?.onChangeState(this.state)
    }

    override fun unbindOutput() {
        this.presenterOutput = null
    }

    override fun onError(throwable: Throwable) {
        this.state = this.state.copy(transient = null, error = State.Error(
            throwable
        )
        )
    }

    protected open fun canPerform(action: Action) : Boolean {
        if (this.state.error != null) {
            return false
        }

        return when (this.state.transient) {
            is State.Pending -> false
            else -> true
        }
    }
}