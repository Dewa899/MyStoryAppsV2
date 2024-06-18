package com.submission.mystoryappsv2.view.maps

import androidx.lifecycle.ViewModel
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    private var currentStoriesWithLocation: List<Story>? = null

    suspend fun getStoriesWithLocation(): List<Story> {
        currentStoriesWithLocation?.let { return it }
        val stories = repository.getStoriesWithLocation()
        currentStoriesWithLocation = stories
        return stories
    }

}
