package io.rm.mvi.service

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction

interface CoroutineService : CoroutineScope {
    fun cancel(binding: KFunction<*>)
    fun cancelAll()
    fun launchJob(
        bindTo: KFunction<*>,
        block: suspend (scope: CoroutineScope) -> Unit
    )

    var defaultCoroutineExceptionHandler: CoroutineExceptionHandler
}

class InteractorCoroutineService(
    override val coroutineContext: CoroutineContext
) : CoroutineService, CoroutineScope {

    override lateinit var defaultCoroutineExceptionHandler: CoroutineExceptionHandler
    private val jobsMap = mutableMapOf<KFunction<*>, Job>()

    override fun cancel(binding: KFunction<*>) {
        val job = this.jobsMap[binding]
        job?.cancel()
    }

    override fun cancelAll() {
        this.jobsMap.forEach {
            it.value.cancel()
        }
    }

    override fun launchJob(
        bindTo: KFunction<*>,
        block: suspend (scope: CoroutineScope) -> Unit
    ) {
        val job = Job()
        jobsMap[bindTo]?.cancel()
        jobsMap[bindTo] = job

        val context: CoroutineContext =
            this.coroutineContext + job + this.defaultCoroutineExceptionHandler

        this.launch(context) {
            block(this)
        }
    }
}