package io.rm.mvi.interactor

import io.rm.mvi.service.CoroutineService
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KFunction

interface InteractorInput<TypeInteractorOutput : InteractorOutput> {
    fun cancel()
    var coroutineService: CoroutineService
    var output: TypeInteractorOutput
}

interface InteractorOutput {
    fun onError(throwable: Throwable)
}

open class Interactor<TypeInteractorOutput : InteractorOutput> :
    InteractorInput<TypeInteractorOutput> {
    override lateinit var coroutineService: CoroutineService
    override lateinit var output: TypeInteractorOutput

    override fun cancel() {
        this.coroutineService.cancelAll()
    }

    protected fun launch(
        bindTo: KFunction<*>,
        block: suspend (CoroutineScope) -> Unit
    ) {
        this.coroutineService.launchJob(bindTo, block)
    }
}

val <T> T.exhaustive: T
    get() = this