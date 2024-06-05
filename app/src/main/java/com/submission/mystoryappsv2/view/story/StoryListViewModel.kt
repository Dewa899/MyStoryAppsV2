package com.submission.mystoryappsv2.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.launch

class StoryListViewModel(private val repository: Repository) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun fetchStories(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStories(token)
                _stories.value = response
            } catch (e: Exception) {
                // Handle exception case
                _stories.value = emptyList() // or handle error appropriately
            }
        }
    }
}