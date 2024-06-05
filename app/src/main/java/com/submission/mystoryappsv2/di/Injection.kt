package com.submission.mystoryappsv2.di

import android.content.Context
import com.submission.mystoryappsv2.data.repository.Repository
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.pref.dataStore
import com.submission.mystoryappsv2.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val dataStore = context.dataStore
        val preferences = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService, preferences)
    }
}
