package com.submission.mystoryappsv2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object FlowTestUtils {
    @OptIn(ExperimentalTime::class)
    fun <T> Flow<T>.getOrAwaitValue(
        time: Duration = 2.seconds,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Main).launch {
            afterObserve.invoke()
            try {
                data = withTimeoutOrNull(time.inWholeMilliseconds) { first() }
                latch.countDown()
            } catch (e: TimeoutException) {
                throw TimeoutException("Flow value was never set.")
            }
        }
        try {
            if (!latch.await(time.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
                throw TimeoutException("Flow value was never set.")
            }
        } finally {
            job.cancel()
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}