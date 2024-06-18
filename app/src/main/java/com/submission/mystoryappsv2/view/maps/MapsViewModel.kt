package com.submission.mystoryappsv2.view.maps

import StoryPagingSource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submission.mystoryappsv2.data.remote.ApiService
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: Repository) : ViewModel() {

    private var currentStoriesWithLocation: List<Story>? = null

    suspend fun getStoriesWithLocation(): List<Story> {
        currentStoriesWithLocation?.let { return it } // Return cached data if available
        val stories = repository.getStoriesWithLocation()
        currentStoriesWithLocation = stories
        return stories
    }

    fun refreshData() {
        currentStoriesWithLocation = null
    }
}
