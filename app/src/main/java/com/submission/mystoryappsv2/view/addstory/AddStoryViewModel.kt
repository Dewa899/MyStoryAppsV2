package com.submission.mystoryappsv2.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.submission.mystoryappsv2.data.remote.ApiResponse
import com.submission.mystoryappsv2.data.repository.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: Repository) : ViewModel() {

    fun addStory(description: RequestBody, photo: MultipartBody.Part): LiveData<ApiResponse> = liveData {
        val response = repository.addStory(description, photo)
        emit(response)
    }
}
