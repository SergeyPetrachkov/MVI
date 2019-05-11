package io.rm.mvisample.core

import android.content.Context
import io.rm.mvi.service.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

interface SafeJobExecutor {
    suspend fun <T : Any> safeCall(
        call: suspend () -> Result<T>,
        context: CoroutineContext,
        errorMessage: String? = null
    ): Result<T>

    fun <T : Any> safeCallAsync(
        call: suspend () -> T,
        scope: CoroutineScope,
        context: CoroutineContext,
        errorMessage: String? = null
    ): Deferred<Result<T>>
}

class AppSafeJobExecutor(private val appContext: Context) : SafeJobExecutor {
    override suspend fun <T : Any> safeCall(
        call: suspend () -> Result<T>,
        context: CoroutineContext,
        errorMessage: String?
    ): Result<T> = withContext(context) {
        try {
            call()
        } catch (e: Throwable) {
            e.printStackTrace()
            val message: String = getMessage(e, errorMessage)
            Result.Error(IOException(message, e))
        }
    }

    override fun <T : Any> safeCallAsync(
        call: suspend () -> T,
        scope: CoroutineScope,
        context: CoroutineContext,
        errorMessage: String?
    ): Deferred<Result<T>> = scope.async(context) {
        try {
            Result.Success(data = call())
        } catch (e: Exception) {
            e.printStackTrace()
            val message: String = getMessage(e, errorMessage)
            Result.Error(IOException(message, e))
        }
    }

    private fun getMessage(e: Throwable, errorMessage: String?): String {
        return when {
            e is SocketTimeoutException || e is UnknownHostException -> {
                this@AppSafeJobExecutor.appContext.getString(R.string.core_error_network)
            }
            errorMessage.isNullOrBlank().not() -> {
                errorMessage!!
            }
            e.message.isNullOrBlank().not() -> {
                e.message!!
            }
            else -> {
                this@AppSafeJobExecutor.appContext.getString(R.string.core_error_unknown)
            }
        }
    }
}