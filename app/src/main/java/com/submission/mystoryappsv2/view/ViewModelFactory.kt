package com.submission.mystoryappsv2.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.pref.dataStore
import com.submission.mystoryappsv2.data.remote.ApiConfig
import com.submission.mystoryappsv2.data.repository.Repository
import com.submission.mystoryappsv2.view.addstory.AddStoryViewModel
import com.submission.mystoryappsv2.view.login.LoginViewModel
import com.submission.mystoryappsv2.view.main.MainViewModel
import com.submission.mystoryappsv2.view.maps.MapsViewModel
import com.submission.mystoryappsv2.view.signup.SignupViewModel

class ViewModelFactory private constructor(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            val apiService = ApiConfig.getApiService()
            val userPreference = UserPreference.getInstance(context.dataStore)
            val repository = Repository.getInstance(apiService, userPreference)
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(repository).also { INSTANCE = it }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
