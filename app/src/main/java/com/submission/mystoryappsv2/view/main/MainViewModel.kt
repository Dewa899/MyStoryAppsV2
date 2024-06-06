package com.submission.mystoryappsv2.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun fetchStories(token: String, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getStories(token, page)
                _stories.value = response
            } catch (e: Exception) {
                _stories.value = emptyList()
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                // Tangani exception di sini, jika diperlukan
                Log.e("MainViewModel", "Failed to logout", e)
                // Misalnya, tampilkan pesan kesalahan kepada pengguna
            }
        }
    }

}