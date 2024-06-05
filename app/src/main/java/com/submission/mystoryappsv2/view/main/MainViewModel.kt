package com.submission.mystoryappsv2.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.submission.mystoryappsv2.data.repository.Repository
import com.submission.mystoryappsv2.data.pref.UserModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    suspend fun getSession(): LiveData<UserModel> {
        return viewModelScope.async {
            repository.getSession().asLiveData()
        }.await()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}