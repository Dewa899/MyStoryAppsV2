package com.submission.mystoryappsv2

import android.util.Log
import org.mockito.MockedStatic
import org.mockito.Mockito

object LogMock {
    private var logMock: MockedStatic<Log>? = null

    fun mockLog() {
        if (logMock == null) {
            logMock = Mockito.mockStatic(Log::class.java)
        }
    }

    fun clearMock() {
        logMock?.close()
        logMock = null
    }
}
