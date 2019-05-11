package io.rm.mvisample.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

interface DefaultCoroutineExceptionHandler : CoroutineExceptionHandler {
    var onError: (Throwable) -> Unit
}

class AppDefaultCoroutineExceptionHandler :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), DefaultCoroutineExceptionHandler {
    override lateinit var onError: (Throwable) -> Unit

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (this::onError.isInitialized) {
            this.onError.invoke(exception)
        }
    }
}