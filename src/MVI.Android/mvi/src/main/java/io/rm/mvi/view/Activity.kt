package io.rm.mvi.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import dagger.android.support.DaggerAppCompatActivity
import io.rm.viper.R
import io.rm.mvi.presenter.CombinedState
import io.rm.mvi.presenter.PresenterInput
import io.rm.mvi.presenter.PresenterOutput
import io.rm.mvi.presenter.State
import io.rm.mvi.router.RouterInput
import javax.inject.Inject

abstract class Activity<TypePresenter : PresenterInput,
        TypeRouter : RouterInput<Activity>> :
    DaggerAppCompatActivity(), PresenterOutput {

    protected abstract var layoutId: Int

    @Inject
    lateinit var presenter: TypePresenter

    @Inject
    lateinit var router: TypeRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(this.layoutId)
    }

    override fun onResume() {
        super.onResume()
        this.presenter.bindOutput(this)
        this.router.bindView(this)
    }

    override fun onPause() {
        super.onPause()
        this.presenter.unbindOutput()
        this.router.unbindView()
    }

    override fun onChangeState(state: CombinedState<*>) {
        Log.d("ViperState", "${this.javaClass.simpleName} ${state.data} ${state.transient} ${state.error}")

        when {
            state.error != null -> {
                onError(state.error)
            }
            state.transient != null -> {
                onTransient(state.transient)
            }
            else -> {
                onData(state.data)
            }
        }
    }

    protected open fun onError(error: State.Error) {
        error.throwable.message?.let {
            this.showErrorMessage(it)
        }
    }

    protected open fun onTransient(transient: State.Transient) {
        when (transient) {
            is State.Pending -> {
                onPending()
            }
            is State.PendingEnd -> {
                onPendingEnd()
            }
        }
    }

    protected open fun onData(data: State.Data) {
        onPendingEnd()
    }

    protected open fun onPending() {

    }

    protected open fun onPendingEnd() {

    }

    protected fun showErrorMessage(message: String) {
        if (!this.isFinishing) {
            val dialogFragment = AlertDialogFragment.newInstance(
                title = this.getString(R.string.viper_alert_dialog_error_title),
                message = message
            )
            dialogFragment.show(
                this.supportFragmentManager,
                AlertDialogFragment::class.java.simpleName
            )
        }
    }
}