package io.rm.mvisample.core.data.remote

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        enqueue(object : Callback<T> { // install callback
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    cont.resume(response.body()!!)
                } else {
                    cont.resumeWithException(HttpException(response))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (cont.isCancelled) return
                cont.resumeWithException(t)
            }
        })
        // cancel Call when continuation is cancelled
        cont.invokeOnCancellation {
            cancel()
        }
    }