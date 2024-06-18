package com.submission.mystoryappsv2

import org.mockito.Mockito
import org.mockito.MockitoAnnotations

object LogMock {
    private var mock: AutoCloseable? = null
    fun mockLog() {
        if (mock == null) {
            mock = MockitoAnnotations.openMocks(this)
            Mockito.mockStatic(android.util.Log::class.java).apply {
                `when`<Int> { android.util.Log.d(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                `when`<Int> { android.util.Log.i(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                `when`<Int> { android.util.Log.e(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
                `when`<Int> { android.util.Log.w(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
            }
        }
    }

    fun clearMock() {
        mock?.close()
        mock = null
    }
}
